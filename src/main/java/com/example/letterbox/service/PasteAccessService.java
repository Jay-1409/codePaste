/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.service;

import com.example.letterbox.entity.Paste;
import com.example.letterbox.exception.PasteAccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasteAccessService implements PasteAccessPolicy {
    private final PasswordEncoder passwordEncoder;

    public PasteAccessService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean isProtected(Paste paste) {
        return Boolean.FALSE.equals(paste.getAccess());
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public void verifyAccess(Paste paste, String password) {
        if (!isProtected(paste)) {
            return;
        }
        if (password == null || password.isBlank() || paste.getPastePass() == null
                || !passwordEncoder.matches(password, paste.getPastePass())) {
            throw new PasteAccessDeniedException();
        }
    }
}
