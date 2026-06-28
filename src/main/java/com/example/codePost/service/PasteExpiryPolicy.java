package com.example.codePost.service;

import com.example.codePost.entity.Paste;

public interface PasteExpiryPolicy {
    Paste requireActive(Paste paste);
}
