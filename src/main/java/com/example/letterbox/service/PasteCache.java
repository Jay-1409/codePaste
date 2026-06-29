/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.service;

import com.example.letterbox.entity.Paste;

public interface PasteCache {
    Paste findById(String pasteId);
    Paste put(Paste paste);
    void evict(String pasteId);
}
