/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.service.impl;

import com.example.letterbox.service.PasteIdGenerator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class custom62BitUidGenerator implements PasteIdGenerator {
    private static final String UID_COUNTER_KEY = "codepaste:uid:counter";
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final long MAX_COUNTER = (1L << 62) - 1;
    private static final long BATCH_SIZE = 1000;

    private final BlockingQueue<String> availableIds = new ArrayBlockingQueue<>((int) BATCH_SIZE);

    public custom62BitUidGenerator(StringRedisTemplate redisTemplate) {
        Thread producer = new Thread(() -> produceIds(redisTemplate));
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

    private void produceIds(StringRedisTemplate redisTemplate) {
        try {
            while (true) {
                long rangeEnd = redisTemplate.opsForValue().increment(UID_COUNTER_KEY, BATCH_SIZE);
                long rangeStart = rangeEnd - BATCH_SIZE + 1;
                if (rangeStart > MAX_COUNTER) {
                    return;
                }

                long usableRangeEnd = Math.min(rangeEnd, MAX_COUNTER);
                for (long nextUid = rangeStart; nextUid <= usableRangeEnd; nextUid++) {
                    availableIds.put(encode(nextUid));
                }
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to generate paste IDs", exception);
        }
    }

    private String encode(long number) {
        if (number < 0 || number > MAX_COUNTER) {
            throw new IllegalArgumentException("Value must be between 0 and " + MAX_COUNTER);
        }

        StringBuilder encoded = new StringBuilder();
        for (int bitPosition = 0; bitPosition < ALPHABET.length(); bitPosition++) {
            if (((number >> bitPosition) & 1) == 1) {
                encoded.append(ALPHABET.charAt(bitPosition));
            }
        }
        return encoded.toString();
    }
}
