/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.service.impl;

import com.example.letterbox.service.UidCodec;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RedisPasteIdGeneratorTest {

    @Test
    void fillsPoolFromRedisReservedRange() throws Exception {
        UidCodec uidCodec = mock(UidCodec.class);
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("codepaste:uid:counter", 1000)).thenReturn(1000L, 2000L);
        when(uidCodec.encode(anyLong()))
                .thenAnswer(invocation -> Optional.of(String.valueOf(invocation.getArgument(0, Long.class))));

        RedisPasteIdGenerator idGenerator = new RedisPasteIdGenerator(uidCodec, redisTemplate);

        assertEquals("1", idGenerator.nextId());
        assertEquals("2", idGenerator.nextId());
    }
}
