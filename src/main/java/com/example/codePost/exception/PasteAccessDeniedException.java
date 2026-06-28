package com.example.codePost.exception;

public class PasteAccessDeniedException extends RuntimeException {
    public PasteAccessDeniedException() {
        super("A valid password is required to access this paste");
    }
}
