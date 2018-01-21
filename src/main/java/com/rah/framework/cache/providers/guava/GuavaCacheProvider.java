package com.rah.framework.cache.providers.guava;

import com.rah.framework.cache.api.CacheProvider;
import com.rah.framework.cache.entities.CacheEntry;
import com.rah.framework.cache.exceptions.EntryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component(value = "guavaCacheProvider")
public class GuavaCacheProvider<T> implements CacheProvider<T> {
    private static final Logger logger = LoggerFactory.getLogger(GuavaCacheProvider.class);
    private GuavaCache cache;

    @Override
    public CacheEntry<T> get(String key) {
        try {
            logger.trace("Getting the cache by key");
            Cache.ValueWrapper vw = cache.get(key);
            return (CacheEntry<T>) vw.get();
        } catch (Exception e) {
            return null;
        } catch (Error e) {
            return null;
        }
    }

    @Override
    public Boolean put(String key, CacheEntry<T> cacheEntry) {
        logger.trace("Putting the cache by key int guava");
        cache.put(key, cacheEntry);
        return true;
    }

    public void setCache(GuavaCache cache) {
        this.cache = cache;
    }

    @Override
    public boolean delete(String key) {
        return false;
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
