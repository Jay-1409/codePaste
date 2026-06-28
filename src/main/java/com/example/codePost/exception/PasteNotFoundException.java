package com.example.codePost.exception;

public class PasteNotFoundException extends RuntimeException {
    public PasteNotFoundException(String pasteId) {
        super("Paste not found: " + pasteId);
    }
}
