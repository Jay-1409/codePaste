/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.service.impl;

import com.example.letterbox.entity.Paste;
import com.example.letterbox.repository.PasteRepository;
import com.example.letterbox.service.PasteCache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RedisPasteCache implements PasteCache {
    private final PasteRepository pasteRepository;

    public RedisPasteCache(PasteRepository pasteRepository) {
        this.pasteRepository = pasteRepository;
    }

    @Override
    @Cacheable(value = "pastes", key = "#pasteId", unless = "#result == null")
    public Paste findById(String pasteId) {
        return pasteRepository.findById(pasteId).orElse(null);
    }

    @Override
    @CachePut(value = "pastes", key = "#paste.pasteId")
    public Paste put(Paste paste) {
        return paste;
    }

    @Override
    @CacheEvict(value = "pastes", key = "#pasteId")
    public void evict(String pasteId) {
    }
}
