package com.example.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Simple in-process cache for reference data (branches) that's read far more often than it
 * changes. Zero extra infrastructure needed. If this needs to scale across multiple app
 * instances later, swap this bean for a RedisCacheManager - nothing else in the codebase
 * needs to change since services only depend on the CacheManager abstraction.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String BRANCHES_CACHE = "branches";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(BRANCHES_CACHE);
    }
}
