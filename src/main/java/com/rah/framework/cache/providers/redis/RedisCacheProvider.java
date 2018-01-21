package com.rah.framework.cache.providers.redis;

import com.rah.framework.cache.api.CacheProvider;
import com.rah.framework.cache.entities.CacheEntry;
import com.rah.framework.cache.exceptions.EntryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisCacheProvider<T> implements CacheProvider<T> {
    private static final Logger logger = LoggerFactory.getLogger(RedisCacheProvider.class);

    RedisTemplate<String, Object> redisTemplate;
    private String                        redisPrefixKey;

    @Override
    public CacheEntry<T> get(String key) {
        try {
            logger.trace("get cache from redis cache " + key);
            return (CacheEntry<T>) redisTemplate.opsForHash().get(redisPrefixKey, key);
        } catch (Exception e) {
            return null;
        } catch (Error e) {
            return null;
        }
    }

    @Override
    public Boolean put(String key, CacheEntry<T> cacheEntry) {
        if (cacheEntry != null) {
            logger.trace("put cache in redis cache " + key);
            redisTemplate.opsForHash().put(redisPrefixKey, key, cacheEntry);
            logger.trace("Post cache entries in redis");
            return true;
        }
        return false;
    }

    @Override
    public String getKeyPrefix() {
        return redisPrefixKey;
    }

    @Override
    public boolean delete(String key) {
        try {
            redisTemplate.opsForHash().delete(redisPrefixKey, key);
        } catch (Exception ex) {
            logger.warn("Eviction for key from redis cache {} failed {} ", key, ex.getCause());
            return false;
        }
        return true;
    }

    public void setRedisPrefixKey(String redisPrefixKey) {
        this.redisPrefixKey = redisPrefixKey;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
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
