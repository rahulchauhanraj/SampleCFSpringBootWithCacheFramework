package com.rah.framework.cache.helper;

public class Ticker {
    public Ticker() {
    }

    public static long read() {
        return System.currentTimeMillis() / 1000L;
    }
}