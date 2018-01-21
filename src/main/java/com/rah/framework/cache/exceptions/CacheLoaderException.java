package com.rah.framework.cache.exceptions;

public class CacheLoaderException extends Exception {
    public CacheLoaderException() {
    }

    public CacheLoaderException(String message) {
        super(message);
    }

    public CacheLoaderException(Throwable cause) {
        super(cause);
    }

    public CacheLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
