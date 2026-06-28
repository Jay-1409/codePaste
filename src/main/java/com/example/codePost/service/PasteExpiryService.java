package com.example.codePost.service;

import com.example.codePost.entity.Paste;
import com.example.codePost.exception.PasteExpiredException;
import com.example.codePost.repository.PasteRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class PasteExpiryService implements PasteExpiryPolicy {
    private final PasteRepository pasteRepository;
    private final PasteCache pasteCache;
    private final Clock clock;

    public PasteExpiryService(PasteRepository pasteRepository, PasteCache pasteCache, Clock clock) {
        this.pasteRepository = pasteRepository;
        this.pasteCache = pasteCache;
        this.clock = clock;
    }

    @Override
    public Paste requireActive(Paste paste) {
        if (!paste.getExpireAfter().isAfter(clock.instant())) {
            try {
                pasteRepository.deleteById(paste.getPasteId());
            } finally {
                pasteCache.evict(paste.getPasteId());
            }
            throw new PasteExpiredException(paste.getPasteId());
        }
        return paste;
    }
}
