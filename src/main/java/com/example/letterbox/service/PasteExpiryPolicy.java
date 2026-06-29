package com.example.letterbox.service;

import com.example.letterbox.entity.Paste;

public interface PasteExpiryPolicy {
    Paste requireActive(Paste paste);
}
