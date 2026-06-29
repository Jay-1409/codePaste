/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.exception;

public class PasteIdConflictException extends RuntimeException {
    public PasteIdConflictException(String pasteId) {
        super("Paste ID is not available: " + pasteId);
    }
}
