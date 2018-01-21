package com.rah.framework.cache.api;

import com.rah.framework.cache.entities.CacheEntry;

public interface CacheProvider<T> {
    CacheEntry<T> get(String var1);

    Boolean put(String var1, CacheEntry<T> var2);

    default public String getKeyPrefix() {
        return "";
    }

    boolean delete(String key);

    void setSynchReload(String key, boolean synchReload);
}
