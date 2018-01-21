package com.rah.framework.cache.exceptions;

public class CacheProviderException extends Exception {
    public CacheProviderException() {
    }

    public CacheProviderException(String message) {
        super(message);
    }

    public CacheProviderException(Throwable cause) {
        super(cause);
    }

    public CacheProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}