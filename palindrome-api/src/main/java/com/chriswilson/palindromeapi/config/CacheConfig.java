package com.chriswilson.palindromeapi.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

  public static final String PALINDROME_CACHE = "palindromeCache";

  @Bean
  @Profile("local")
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager();
  }

  @Bean
  @Profile("scalable")
  public CacheManager redisCacheManager(RedisConnectionFactory lettuceConnectionFactory) {
    RedisCacheConfiguration cacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1));
    return RedisCacheManager.builder(lettuceConnectionFactory)
        .cacheDefaults(cacheConfiguration)
        .build();
  }
}
