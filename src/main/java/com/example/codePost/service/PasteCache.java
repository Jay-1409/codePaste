package com.example.codePost.service;

import com.example.codePost.entity.Paste;

public interface PasteCache {
    Paste findById(String pasteId);
    Paste put(Paste paste);
    void evict(String pasteId);
}
