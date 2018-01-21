package com.rah.framework.cache.providers.caffeine;

import com.rah.framework.cache.api.CacheProvider;
import com.rah.framework.cache.entities.CacheEntry;
import com.rah.framework.cache.exceptions.EntryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;

public class CaffeineCacheProvider<T> implements CacheProvider<T> {
    private static final Logger logger = LoggerFactory.getLogger(CaffeineCacheProvider.class);
    private CaffeineCache caffeineCache;

    @Override
    public CacheEntry<T> get(String key) {
        try {
            logger.trace("Getting the cache by key from caffeine for " + key);
            Cache.ValueWrapper vw = caffeineCache.get(key);
            return (CacheEntry<T>) vw.get();
        } catch (Exception e) {
            return null;
        } catch (Error e) {
            return null;
        }
    }

    @Override
    public Boolean put(String key, CacheEntry<T> cacheEntry) {
        if (cacheEntry != null) {
            logger.trace("Putting the cache by key into caffeine " + key);
            caffeineCache.put(key, cacheEntry);
            return true;
        }
        return false;
    }

    public void setCache(CaffeineCache cache) {
        this.caffeineCache = cache;
    }

    @Override
    public boolean delete(String key) {
        try {
            caffeineCache.evict(key);
        } catch (Exception ex) {
            logger.warn("Eviction for key from caffeine cache {} failed {} ", key, ex.getCause());
            return false;
        }
        return true;
    }

    @Override
    public void setSynchReload(String key, boolean synchReload) {
        CacheEntry cacheEntry = this.get(key);

        if (cacheEntry == null) {
            throw new EntryNotFoundException(String.format("Entry not found for key : %s", key));
        }
        cacheEntry.setSynchReload(synchReload);
    }
}
