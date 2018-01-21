package com.rah.framework.cache.api;

import com.rah.framework.cache.entities.CacheEntry;
import com.rah.framework.cache.entities.CompositeKey;
import com.rah.framework.cache.exceptions.CacheLoaderException;

public interface CacheLoader<T> {
    CacheEntry<T> reload(CompositeKey compositeKey, T var2) throws CacheLoaderException;
}
