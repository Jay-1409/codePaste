package com.example.codePost.entity;

import reactor.util.annotation.NonNull;

import java.time.Instant;

public class PasteBody {
    @NonNull
    private String pasteId = "";
    @NonNull
    private String paste = "";
    private Long expireAfter = null;
    private Boolean access = true;
    private Boolean isPassProtected = false;
    private String pastePass = null;


    public String getPastePass() {
        return pastePass;
    }

    public void setPastePass(String pastePass) {
        this.pastePass = pastePass;
    }

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

    public Long getExpireAfter() {
        return expireAfter;
    }

    public void setExpireAfter(Long expireAfter) {
        this.expireAfter = expireAfter;
    }

    public Boolean getAccess() {
        return access;
    }

    public void setAccess(Boolean access) {
        this.access = access;
    }

    public Boolean getPassProtected() {
        return isPassProtected;
    }

    public void setPassProtected(Boolean passProtected) {
        isPassProtected = passProtected;
    }
}
