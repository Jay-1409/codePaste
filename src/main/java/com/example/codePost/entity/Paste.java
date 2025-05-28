package com.example.codePost.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.jetbrains.annotations.NotNull;
import java.io.Serializable;

import java.time.Duration;
import java.time.Instant;

@Document(collection = "paste")
public class Paste implements Serializable {
    @NotNull
    @Id
    private String pasteId = "";

    @NotNull
    private String paste = "";

    private String pastePass;

    @NotNull
    private Boolean access = true;

    @NotNull
    private Instant expireAfter = Instant.now().plus(Duration.ofDays(30));

//    Boolean isPassProtected = false;

    @NotNull
    public String getPasteId() {
        return pasteId;
    }

    public void setPasteId(@NotNull String pasteId) {
        this.pasteId = pasteId;
    }

    @NotNull
    public String getPaste() {
        return paste;
    }

    public void setPaste(@NotNull String paste) {
        this.paste = paste;
    }

    public String getPastePass() {
        return pastePass;
    }

    public void setPastePass(String pastePass) {
        this.pastePass = pastePass;
    }

    @NotNull
    public Boolean getAccess() {
        return access;
    }

    public void setAccess(@NotNull Boolean access) {
        this.access = access;
    }

    @NotNull
    public Instant getExpireAfter() {
        return expireAfter;
    }

    public void setExpireAfter(@NotNull Instant expireAfter) {
        this.expireAfter = expireAfter;
    }

//    public Boolean getPassProtected() {
//        return isPassProtected;
//    }
//
//    public void setPassProtected(Boolean passProtected) {
//        isPassProtected = passProtected;
//    }
}

// basic architecture:
// Pate data -> mongodb
// dirty bit -> mongodb -> on startup fetch -> cache -> batch sync
// visCount -> mongodb -> on startup fetch -> cache -> batch sync
