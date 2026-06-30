/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class custom62BitUidGeneratorTest {

    @Test
    void fillsPoolFromRedisReservedRange() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("codepaste:uid:counter", 1000)).thenReturn(1000L, 2000L);

        custom62BitUidGenerator idGenerator = new custom62BitUidGenerator(redisTemplate);

        assertEquals("0", idGenerator.nextId());
        assertEquals("1", idGenerator.nextId());
    }
}
