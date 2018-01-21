package com.rah.sample.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.rah.sample.util.Constants.SAMPLE_DATA_OBJECT_CACHE;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfiguration.class);

    private @Value("${SAMPLE_CACHE_EXPIRE_TIME_SECONDS:60}")
    int sampleCacheExpireTimeSeconds;

    private @Value("${SAMPLE_CACHE_MAX_SIZE:100}")
    int sampleCacheMaxSize;

    @Bean
    public CaffeineCache sampleCaffeineCache() {
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("TemplateCasesFormsCache : ExpireTime=[%6d] MaxCacheSize=[%6d] entries", sampleCacheExpireTimeSeconds, sampleCacheMaxSize));
        }
        return new CaffeineCache(SAMPLE_DATA_OBJECT_CACHE, Caffeine.newBuilder().maximumSize(sampleCacheMaxSize).build());
    }
}
