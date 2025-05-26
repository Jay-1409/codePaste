package com.example.codePost.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import reactor.util.annotation.NonNull;

import java.time.Duration;
import java.time.Instant;

@Document(collection = "paste")
public class Paste {
    @NonNull
    private String pasteId = "";

    @NonNull
    private String paste = "";

    private String pastePass;

    @NonNull
    private Boolean access = Boolean.TRUE;

    @NonNull
    private Instant expireAfter = Instant.now().plus(Duration.ofDays(1));
    @NonNull
    public String getPasteId() {
        return pasteId;
    }

    public void setPasteId(@NonNull String pasteId) {
        this.pasteId = pasteId;
    }

    @NonNull
    public String getPaste() {
        return paste;
    }

    public void setPaste(@NonNull String paste) {
        this.paste = paste;
    }

    public String getPastePass() {
        return pastePass;
    }

    public void setPastePass(String pastePass) {
        this.pastePass = pastePass;
    }

    @NonNull
    public Boolean getAccess() {
        return access;
    }

    public void setAccess(@NonNull Boolean access) {
        this.access = access;
    }

    @NonNull
    public Instant getExpireAfter() {
        return expireAfter;
    }

    public void setExpireAfter(@NonNull Instant expireAfter) {
        this.expireAfter = expireAfter;
    }
}

// basic architecture:
// Pate data -> mongodb
// dirty bit -> mongodb -> on startup fetch -> cache -> batch sync
// visCount -> mongodb -> on startup fetch -> cache -> batch sync
