/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.service;

import com.example.letterbox.entity.Paste;
import com.example.letterbox.entity.PasteBody;
import com.example.letterbox.exception.PasteNotFoundException;
import com.example.letterbox.repository.PasteRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
public class PasteService {
    private final PasteRepository pasteRepository;
    private final PasteIdGenerator pasteIdGenerator;
    private final PasteCache pasteCache;
    private final PasteExpiryPolicy pasteExpiryPolicy;
    private final PasteAccessPolicy pasteAccessPolicy;
    private final Clock clock;

    public PasteService(
            PasteRepository pasteRepository,
            PasteIdGenerator pasteIdGenerator,
            PasteCache pasteCache,
            PasteExpiryPolicy pasteExpiryPolicy,
            PasteAccessPolicy pasteAccessPolicy,
            Clock clock
    ) {
        this.pasteRepository = pasteRepository;
        this.pasteIdGenerator = pasteIdGenerator;
        this.pasteCache = pasteCache;
        this.pasteExpiryPolicy = pasteExpiryPolicy;
        this.pasteAccessPolicy = pasteAccessPolicy;
        this.clock = clock;
    }
    /**
     * @param paste the paste to add
     * @return the added paste  
     */
    public Paste addPaste(@NotNull PasteBody paste) {
        Paste newPaste = new Paste();
        String pasteId = paste.getPasteId() == null || paste.getPasteId().isBlank()
                ? pasteIdGenerator.nextId()
                : paste.getPasteId();

        newPaste.setPasteId(pasteId);
        newPaste.setPaste(paste.getPaste());
        newPaste.setAccess(paste.getAccess());

        if (Boolean.FALSE.equals(paste.getAccess()) && paste.getPastePass() != null) {
            newPaste.setPastePass(pasteAccessPolicy.encodePassword(paste.getPastePass()));
        }

        Instant expiration = paste.getExpireAfter() == null
                ? clock.instant().plus(Duration.ofDays(30))
                : clock.instant().plus(Duration.ofDays(paste.getExpireAfter()));
        newPaste.setExpireAfter(expiration);

        Paste savedPaste = pasteRepository.insert(newPaste);
        pasteCache.put(savedPaste);
        return savedPaste;
    }

    public boolean checkIfIdExists(String pasteId) {
        Paste paste = pasteCache.findById(pasteId);
        if (paste == null) {
            return false;
        }
        pasteExpiryPolicy.requireActive(paste);
        return true;
    }

    public boolean checkIfPassProtected(String pasteId) {
        return pasteAccessPolicy.isProtected(getActivePaste(pasteId));
    }
    /**
     * @param pasteId the paste id
     * @param password the password
     * @return the paste  
     */
    public Paste getPaste(String pasteId, String password) {
        Paste paste = getActivePaste(pasteId);
        pasteAccessPolicy.verifyAccess(paste, password);
        return paste;
    }

    /**
     * @param pasteId the paste id
     * @return true if the paste was deleted successfully  
     */
    public boolean deletePaste(String pasteId) {
        Paste paste = getActivePaste(pasteId);
        pasteRepository.deleteById(paste.getPasteId());
        pasteCache.evict(paste.getPasteId());
        return true;
    }
    /**
     * @param pasteId the paste id
     * @return the paste  
     * @throws PasteNotFoundException if the paste is not found
     */
    private Paste getActivePaste(String pasteId) {
        Paste paste = pasteCache.findById(pasteId);
        if (paste == null) {
            throw new PasteNotFoundException(pasteId);
        }
        return pasteExpiryPolicy.requireActive(paste);
    }
}
