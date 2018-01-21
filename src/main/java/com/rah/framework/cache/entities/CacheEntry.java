package com.rah.framework.cache.entities;

import com.rah.framework.cache.helper.Ticker;

import java.io.Serializable;

public final class CacheEntry<T> implements Serializable {
    private final   T            value;
    private final   long         ttl;
    private final   long         epoch_timestamp;
    private         boolean      synchReload;
    private         CompositeKey compositeKey;

    @SuppressWarnings("unused")
    public CacheEntry() {
        this.ttl = 0L;
        this.epoch_timestamp = 0L;
        this.compositeKey = null;
        this.value = null;
    }

    public CacheEntry(CompositeKey compositeKey, T value, long ttl) {
        this.compositeKey = compositeKey;
        this.value = value;
        this.ttl = ttl;
        this.epoch_timestamp = Ticker.read();
    }

    public CacheEntry(CompositeKey compositeKey, T value) {
        this.compositeKey = compositeKey;
        this.value = value;
        this.ttl = 0L;
        this.epoch_timestamp = 0L;
    }

    public T getValue() {
        return this.value;
    }

    public long getTtl() {
        return this.ttl;
    }

    public long getEpoch_timestamp() {
        return this.epoch_timestamp;
    }

    public CompositeKey getCompositeKey() {
        return compositeKey;
    }

    public void setCompositeKey(CompositeKey compositeKey) {
        this.compositeKey = compositeKey;
    }

    public boolean isSynchReload() {
        return synchReload;
    }

    public void setSynchReload(boolean synchReload) {
        this.synchReload = synchReload;
    }

    @Override
    public String toString() {
        return "CacheEntry{" +
            "value=" + ((value == null) ? "null" : value.toString()) +
            ", compositeKey=" + ((compositeKey == null) ? "null" : compositeKey.toString()) +
            ", ttl=" + ttl +
            ", epoch_timestamp=" + epoch_timestamp +
            ", synchReload=" + synchReload +
            '}';
    }
}

