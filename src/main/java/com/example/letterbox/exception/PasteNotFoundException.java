package com.example.letterbox.exception;

public class PasteNotFoundException extends RuntimeException {
    public PasteNotFoundException(String pasteId) {
        super("Paste not found: " + pasteId);
    }
}
