/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.configuration;

import com.example.letterbox.entity.Paste;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.time.Clock;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, Clock clock) {
        RedisCacheConfiguration defaultConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(5));

        RedisCacheConfiguration pasteConfiguration = defaultConfiguration.entryTtl((key, value) -> {
            if (value instanceof Paste paste) {
                Duration remainingLifetime = Duration.between(clock.instant(), paste.getExpireAfter());
                return !remainingLifetime.isNegative() && !remainingLifetime.isZero()
                        ? remainingLifetime
                        : Duration.ofMillis(1);
            }
            return Duration.ofMinutes(5);
        });

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfiguration)
                .withCacheConfiguration("pastes", pasteConfiguration)
                .build();
    }
}
