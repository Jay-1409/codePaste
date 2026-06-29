package com.example.letterbox.service;

import com.example.letterbox.entity.Paste;

public interface PasteCache {
    Paste findById(String pasteId);
    Paste put(Paste paste);
    void evict(String pasteId);
}
