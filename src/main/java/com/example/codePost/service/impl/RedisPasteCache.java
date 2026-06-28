package com.example.codePost.service.impl;

import com.example.codePost.entity.Paste;
import com.example.codePost.repository.PasteRepository;
import com.example.codePost.service.PasteCache;
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
