package com.example.codePost.exception;

public class PasteIdConflictException extends RuntimeException {
    public PasteIdConflictException(String pasteId) {
        super("Paste ID is not available: " + pasteId);
    }
}
