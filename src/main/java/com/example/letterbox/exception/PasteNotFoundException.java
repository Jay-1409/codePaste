/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.exception;

public class PasteNotFoundException extends RuntimeException {
    public PasteNotFoundException(String pasteId) {
        super("Paste not found: " + pasteId);
    }
}
