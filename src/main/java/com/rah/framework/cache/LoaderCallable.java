package com.rah.framework.cache;

import com.rah.framework.cache.api.CacheLoader;
import com.rah.framework.cache.api.CacheProvider;
import com.rah.framework.cache.bookkeeper.CacheBookKeeper;
import com.rah.framework.cache.entities.CacheEntry;
import com.rah.framework.cache.helper.CacheUtil;

import java.util.concurrent.Callable;

public class LoaderCallable<T> implements Callable {
    private final CacheLoader<T>     cacheLoader;
    private final CacheProvider<T>   cacheProvider;
    private final CacheEntry<T>      cacheEntry;
    private final CacheBookKeeper<T> cacheBookKeeper;

    public LoaderCallable(CacheBookKeeper<T> cacheBookKeeper, CacheProvider<T> cacheProvider, CacheLoader<T> cacheLoader, CacheEntry<T> cacheEntry) {
        this.cacheLoader = cacheLoader;
        this.cacheProvider = cacheProvider;
        this.cacheEntry = cacheEntry;
        this.cacheBookKeeper = cacheBookKeeper;
    }

    public Object call() throws Exception {
        return CacheUtil.reloadCacheEntry(cacheEntry, cacheLoader, cacheProvider, cacheBookKeeper);
    }
}
