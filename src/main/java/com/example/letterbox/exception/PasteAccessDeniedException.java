/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.exception;

public class PasteAccessDeniedException extends RuntimeException {
    public PasteAccessDeniedException() {
        super("A valid password is required to access this paste");
    }
}
