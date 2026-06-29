/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.service;

import com.example.letterbox.entity.Paste;

public interface PasteExpiryPolicy {
    Paste requireActive(Paste paste);
}
