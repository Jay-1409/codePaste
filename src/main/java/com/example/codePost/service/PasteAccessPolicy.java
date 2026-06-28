package com.example.codePost.service;

import com.example.codePost.entity.Paste;

public interface PasteAccessPolicy {
    boolean isProtected(Paste paste);
    String encodePassword(String password);
    void verifyAccess(Paste paste, String password);
}
