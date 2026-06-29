/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.service.impl;

import com.example.letterbox.service.PasteIdGenerator;
import com.example.letterbox.service.UidCodec;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class RedisPasteIdGenerator implements PasteIdGenerator {
    private static final String UID_COUNTER_KEY = "codepaste:uid:counter";
    private static final long MAX_COUNTER = (1L << 62) - 1;
    private static final long BATCH_SIZE = 1000;

    private final BlockingQueue<String> availableIds = new ArrayBlockingQueue<>((int) BATCH_SIZE);

    public RedisPasteIdGenerator(UidCodec uidCodec, StringRedisTemplate redisTemplate) {
        Thread producer = new Thread(() -> produceIds(uidCodec, redisTemplate));
        producer.setDaemon(true);
        producer.setName("paste-id-producer");
        producer.start();
    }

    @Override
    public String nextId() {
        try {
            return availableIds.take();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for a paste ID", exception);
        }
    }

    private void produceIds(UidCodec uidCodec, StringRedisTemplate redisTemplate) {
        try {
            while (true) {
                long rangeEnd = redisTemplate.opsForValue().increment(UID_COUNTER_KEY, BATCH_SIZE);
                long rangeStart = rangeEnd - BATCH_SIZE + 1;
                if (rangeStart > MAX_COUNTER) {
                    return;
                }

                long usableRangeEnd = Math.min(rangeEnd, MAX_COUNTER);
                for (long nextUid = rangeStart; nextUid <= usableRangeEnd; nextUid++) {
                    availableIds.put(uidCodec.encode(nextUid).orElseThrow());
                }
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to generate paste IDs", exception);
        }
    }
}
