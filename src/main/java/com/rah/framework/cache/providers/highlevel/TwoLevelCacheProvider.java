package com.rah.framework.cache.providers.highlevel;

import com.rah.framework.cache.api.CacheProvider;
import com.rah.framework.cache.entities.CacheEntry;
import com.rah.framework.cache.exceptions.CacheProviderException;
import com.rah.framework.cache.exceptions.EntryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.rah.framework.cache.helper.CacheUtil.cacheEntryExpired;

public class TwoLevelCacheProvider<T> implements CacheProvider<T> {
    private static final Logger logger = LoggerFactory.getLogger(TwoLevelCacheProvider.class);
    private CacheProvider<T> primaryCache;
    private CacheProvider<T> secondaryCache;

    public TwoLevelCacheProvider(CacheProvider<T> primaryCache, CacheProvider<T> secondaryCache) {
        this.primaryCache = primaryCache;
        this.secondaryCache = secondaryCache;
    }

    /**
     * Attempts to get the value for a key from the primary cache. If the value is not present
     * in the primary cache, it attempts to get the value from the fallback cache.
     *
     * @param key
     * @return value of present, else null
     * @throws CacheProviderException
     */
    @Override
    public CacheEntry<T> get(String key) {
        logger.debug("get cache from two level cache (Caffeine first)" + key);
        CacheEntry<T> cacheEntry;
        cacheEntry = secondaryCache.get(primaryCache.getKeyPrefix() + key);
        if (cacheEntry == null || cacheEntryExpired(cacheEntry)) {
            logger.debug("Inside cacheEntry == null || cacheEntryExpired(cacheEntry)");
            cacheEntry = primaryCache.get(key);
            secondaryCache.put(primaryCache.getKeyPrefix() + key, cacheEntry);
        }
        return cacheEntry;
    }

    @Override
    public Boolean put(String key, CacheEntry<T> value) {
        boolean primaryPut = false;
        if (value != null) {
            logger.trace("put cacheEntry in two level cache " + key);
            primaryPut = primaryCache.put(key, value);
            secondaryCache.put(primaryCache.getKeyPrefix() + key, value);
        }
        return primaryPut;
    }

    public void setPrimaryCache(CacheProvider<T> primaryCache) {
        this.primaryCache = primaryCache;
    }

    public void setSecondaryCache(CacheProvider<T> secondaryCache) {
        this.secondaryCache = secondaryCache;
    }

    @Override
    public boolean delete(String key) {
        try {
            primaryCache.delete(key);
            secondaryCache.delete(primaryCache.getKeyPrefix() + key);
        } catch (Exception ex) {
            logger.warn("Eviction for key from cache {} failed {} ", key, ex.getCause());
            return false;
        }
        return true;
    }

    @Override
    public void setSynchReload(String key, boolean synchReload) {
        CacheEntry cacheEntry = primaryCache.get(key);

        if (cacheEntry == null) {
            throw new EntryNotFoundException(String.format("Entry not found for key : %s", key));
        }
        cacheEntry.setSynchReload(synchReload);
    }
}
