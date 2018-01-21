package com.rah.framework.cache.helper;

import com.rah.framework.cache.api.CacheLoader;
import com.rah.framework.cache.api.CacheProvider;
import com.rah.framework.cache.bookkeeper.CacheBookKeeper;
import com.rah.framework.cache.entities.CacheEntry;
import com.rah.framework.cache.exceptions.CacheProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheUtil {
    private static final Logger logger = LoggerFactory.getLogger(CacheUtil.class);

    public static boolean cacheEntryExpired(CacheEntry cacheEntry) {
        if (cacheEntry.getTtl() == 0L) {
            return false;
        } else {
            long entryTimeStamp = cacheEntry.getEpoch_timestamp();
            long currentTime = Ticker.read();
            long ttl = cacheEntry.getTtl();
            Boolean hasExpired = entryTimeStamp + ttl <= currentTime;
            logger.debug("CacheUtil::cacheEntryExpired? " + hasExpired);
            return hasExpired;
        }
    }

    public static boolean reloadCacheEntry(CacheEntry cacheEntry, CacheLoader cacheLoader, CacheProvider cacheProvider, CacheBookKeeper cacheBookKeeper) {
        Boolean status = false;

        try {
            logger.trace("\n CacheEntry.getCompositeKey(): " + cacheEntry.getCompositeKey().toString());
            CacheEntry newCacheEntry = cacheLoader.reload(cacheEntry.getCompositeKey(), cacheEntry.getValue());
            logger.trace("After reload");
            status = cacheProvider.put(cacheEntry.getCompositeKey().getKey(), newCacheEntry);
            logger.trace("After cacheProvider.put:status is " + status);
        } catch (Exception ex) {
            logger.error("Exception occurred. " + ex);
            logger.trace("Resetting cache to use previous value");
            status = cacheProvider.put(cacheEntry.getCompositeKey().getKey(), cacheEntry);
        } finally {
            logger.debug("Inside finally block");
            logger.trace("Removing request in flight");
            cacheBookKeeper.removeRequestInFlight(cacheEntry);
        }

        return status;
    }
}
