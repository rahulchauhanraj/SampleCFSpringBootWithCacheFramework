package com.rah.sample.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
public class RedisDataSourceConfiguration extends AbstractCloudConfig {

    private @Value("${vcap.services.${REDIS_SERVICE_NAME:rsc-redis}.credentials.host:localhost}")
    String redisHost;

    private @Value("${vcap.services.${REDIS_SERVICE_NAME:rsc-redis}.credentials.port:6379}")
    int redisPort;

    private @Value("${vcap.services.${REDIS_SERVICE_NAME:rsc-redis}.credentials.password:foobared}")
    String redisPassword;

    private @Value("${REDIS_MIN_IDLE:0}")
    int minIdle;

    private @Value("${REDIS_MAX_TOTAL:100}")
    int maxTotal;

    private @Value("${REDIS_MAX_WAIT_TIME_MILLISECONDS:2000}")
    int maxWaitTimeMilliseconds;

    private @Value("${REDIS_SOCKET_TIMEOUT_MILLISECONDS:3000}")
    int soTimeoutMilliseconds;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxWaitMillis(maxWaitTimeMilliseconds);

        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(false);
        jedisPoolConfig.setTestWhileIdle(false);

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
        jedisConnectionFactory.setHostName(redisHost);
        jedisConnectionFactory.setPort(redisPort);
        jedisConnectionFactory.setPassword(redisPassword);
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setTimeout(soTimeoutMilliseconds);

        return jedisConnectionFactory;
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;
    }
}
