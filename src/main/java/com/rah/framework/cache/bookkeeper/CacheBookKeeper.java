package com.rah.framework.cache.bookkeeper;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rah.framework.cache.LoaderCallable;
import com.rah.framework.cache.api.CacheLoader;
import com.rah.framework.cache.api.CacheProvider;
import com.rah.framework.cache.entities.CacheEntry;
import com.rah.framework.cache.entities.CompositeKey;
import com.rah.framework.cache.exceptions.CacheProviderException;
import com.rah.framework.cache.helper.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.rah.framework.cache.helper.CacheUtil.cacheEntryExpired;

public class CacheBookKeeper<T> {
    private static final Logger logger = LoggerFactory.getLogger(CacheBookKeeper.class);

    private final CacheLoader<T>                 cacheLoader;
    private final CacheProvider<T>               cacheProvider;
    private final ExecutorService                executorService;
    private final ConcurrentMap<String, Boolean> requestsInFlight;

    public CacheBookKeeper(CacheProvider<T> cacheProvider, CacheLoader<T> cacheLoader, int threadPoolSize, int queueSize) {
        this.cacheProvider = cacheProvider;
        this.cacheLoader = cacheLoader;
        this.executorService = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(queueSize), (new ThreadFactoryBuilder()).setDaemon(false).setNameFormat("fk-cacheBookKeeper-pool-%d").setPriority(5).build());
        this.requestsInFlight = new ConcurrentHashMap();
    }

    public T get(CompositeKey compositeKey) {
        CacheEntry<T> cacheEntry = this.cacheProvider.get(compositeKey.getKey());

        if (cacheEntry != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("cacheEntry != null");
                logger.debug(String.format("%n cacheEntry : %s", cacheEntry.toString()));
            }
            boolean cacheExpired = cacheEntryExpired(cacheEntry);
            boolean synchReload = cacheEntry.isSynchReload();

            if (cacheExpired) {
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Setting CompositeKey to: %s", compositeKey.toString()));
                    }
                    cacheEntry.setCompositeKey(compositeKey);

                    boolean requestInFlight = this.putRequestInFlight(cacheEntry);

                    if (requestInFlight) {
                        if (synchReload) {
                            boolean status = this.reloadCacheEntrySynch(cacheEntry);
                            if (status) {
                                cacheEntry = this.cacheProvider.get(compositeKey.getKey());
                            }
                        } else {
                            this.reloadCacheEntryAsynch(cacheEntry);
                        }
                    }
                } catch (Exception e) {
                    logger.error(String.format("Error reloading Cache for key: %s", compositeKey.toString()), e);
                    this.removeRequestInFlight(cacheEntry);
                } finally {
                    cacheEntry.setSynchReload(false);
                }
            }

            return cacheEntry.getValue();
        } else {
            return null;
        }
    }

    public boolean put(String key, CacheEntry<T> value) {
        return this.cacheProvider.put(key, value);
    }

    public void expire(CompositeKey compositeKey) {
        Object value = this.get(compositeKey);
        CacheEntry cacheEntry = new CacheEntry(compositeKey, value, -10L);
        this.put(compositeKey.getKey(), cacheEntry);
        this.get(compositeKey);
    }

    private void reloadCacheEntryAsynch(CacheEntry cacheEntry) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("%n inside CacheBookKeeper::reloadCacheEntry %s", cacheEntry.toString()));
        }
        LoaderCallable loaderCallable = new LoaderCallable(this, this.cacheProvider, this.cacheLoader, cacheEntry);
        logger.debug("\n After new LoaderCallable()");
        logger.debug("\n executorService.submit(loaderCallable)");
        this.executorService.submit(loaderCallable);

    }

    private boolean putRequestInFlight(CacheEntry<T> cacheEntry) {
        Object returnValue = this.requestsInFlight.putIfAbsent(cacheEntry.getCompositeKey().getKey(), true);
        Boolean putRequest = returnValue == null;
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("CacheBookKeeper::putRequestInFlight? %b", putRequest));
        }
        return putRequest;
    }

    public void removeRequestInFlight(CacheEntry<T> cacheEntry) {
        this.requestsInFlight.remove(cacheEntry.getCompositeKey().getKey());
    }

    public void delete(String key) {
        cacheProvider.delete(key);
    }

    public void setSynchReload(CompositeKey compositeKey, boolean synchReload) {
        this.cacheProvider.setSynchReload(compositeKey.getKey(), synchReload);
    }

    private boolean reloadCacheEntrySynch (CacheEntry<T> cacheEntry) throws CacheProviderException {
        return CacheUtil.reloadCacheEntry(cacheEntry, cacheLoader, cacheProvider, this);
    }
}

