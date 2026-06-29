/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.service;

import com.example.letterbox.entity.Paste;

public interface PasteAccessPolicy {
    boolean isProtected(Paste paste);
    String encodePassword(String password);
    void verifyAccess(Paste paste, String password);
}
