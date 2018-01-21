package com.rah.sample.cache;

import com.rah.framework.cache.api.CacheLoader;
import com.rah.framework.cache.bookkeeper.CacheBookKeeper;
import com.rah.framework.cache.entities.CacheEntry;
import com.rah.framework.cache.entities.CompositeKey;
import com.rah.framework.cache.providers.caffeine.CaffeineCacheProvider;
import com.rah.framework.cache.providers.highlevel.TwoLevelCacheProvider;
import com.rah.framework.cache.providers.redis.RedisCacheProvider;
import com.rah.sample.models.DataObject;
import com.rah.sample.repository.ISampleRestDataRepository;
import com.rah.sample.util.JsonUtils;
import feign.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static com.rah.sample.util.Constants.SAMPLE_DATA_OBJECT_CACHE;
import static com.rah.sample.util.SampleUtils.getCacheKey;

@Component
public class SampleCacheLoader implements CacheLoader<DataObject> {

    @Inject CaffeineCache                 sampleCaffeineCache;
    @Inject RedisTemplate<String, Object> redisTemplate;
    @Inject ISampleRestDataRepository     sampleRestDataRepository;

    private @Value("${SAMPLE_CACHE_EXPIRE_TIME_SECONDS:60}")
    int sampleCacheExpireTimeSeconds;

    private @Value("${SAMPLE_CACHE_POOL_SIZE:5}")
    int sampleCachePoolSize;

    private @Value("${SAMPLE_CACHE_QUEUE_SIZE:100}")
    int sampleCacheQueueSize;

    private CacheBookKeeper<DataObject> sampleCacheBookKeeper;

    @PostConstruct
    private void autoWire() {
        RedisCacheProvider<DataObject> sampleRedisCacheProvider = new RedisCacheProvider<>();
        sampleRedisCacheProvider.setRedisTemplate(redisTemplate);
        sampleRedisCacheProvider.setRedisPrefixKey(SAMPLE_DATA_OBJECT_CACHE);

        CaffeineCacheProvider<DataObject> sampleCaffeineCacheProvider = new CaffeineCacheProvider<>();
        sampleCaffeineCacheProvider.setCache(sampleCaffeineCache);
        sampleCacheBookKeeper = new CacheBookKeeper<>(new TwoLevelCacheProvider<>(sampleRedisCacheProvider, sampleCaffeineCacheProvider), this, sampleCachePoolSize, sampleCacheQueueSize);
    }

    public DataObject getObject(String authorizationToken, String tenantUuid, String sourceKey) {
        CompositeKey compositeKey = getCompositeKey(authorizationToken, tenantUuid, sourceKey);
        DataObject dataObject;

        try {
            sampleCacheBookKeeper.setSynchReload(compositeKey, true);
            dataObject = sampleCacheBookKeeper.get(compositeKey);

            if (dataObject == null) {
                dataObject = findObject(authorizationToken, tenantUuid, sourceKey);

                if (dataObject != null) {
                    CacheEntry<DataObject> dataObjectEntry = new CacheEntry<>(compositeKey, dataObject, sampleCacheExpireTimeSeconds);
                    sampleCacheBookKeeper.put(compositeKey.getKey(), dataObjectEntry);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to fetch dataObject information for tenantUuid " + tenantUuid, ex);
        }
        return dataObject;
    }

    private DataObject findObject(String authorizationToken, String tenantUuid, String sourceKey) {
        DataObject dataObject;
        try {
            Response response = sampleRestDataRepository.getDataObject(authorizationToken, tenantUuid, sourceKey);
            dataObject = JsonUtils.readValue(response, DataObject.class);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to fetch dataObject information for tenantUuid " + tenantUuid, ex);
        }
        return dataObject;
    }

    @Override
    public CacheEntry<DataObject> reload(CompositeKey compositeKey, DataObject previousValue) {
        DataObject value;
        try {
            value = findObject(compositeKey.getParam("AUTH_TOKEN").toString(), compositeKey.getParam("TENANT_UUID").toString(), compositeKey.getParam("SOURCE_KEY").toString());
            if (value == null) {
                value = previousValue;
            }
        } catch (Exception e) {
            value = previousValue;
        }
        return new CacheEntry<>(compositeKey, value, sampleCacheExpireTimeSeconds);
    }

    private CompositeKey getCompositeKey(String authorizationToken, String tenantUuid, String sourceKey) {
        String cacheKey = getCacheKey(tenantUuid, sourceKey);
        CompositeKey compositeKey = new CompositeKey();
        compositeKey.setKey(cacheKey);
        compositeKey.setParam("AUTH_TOKEN", authorizationToken);
        compositeKey.setParam("TENANT_UUID", tenantUuid);
        compositeKey.setParam("SOURCE_KEY", sourceKey);
        return compositeKey;
    }
}
