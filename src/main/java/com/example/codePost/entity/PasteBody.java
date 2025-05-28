package com.example.codePost.entity;

import org.jetbrains.annotations.NotNull;

public class PasteBody {
    private String pasteId = "";

    @NotNull
    private String paste = "";

    private Long expireAfter = null;

    private Boolean access = true;

//    private Boolean isPassProtected = false;

    private String pastePass = null;

    public String getPastePass() {
        return pastePass;
    }

    public void setPastePass(String pastePass) {
        this.pastePass = pastePass;
    }

    public String getPasteId() {
        return pasteId;
    }

    public void setPasteId(String pasteId) {
        this.pasteId = pasteId;
    }

    public String getPaste() {
        return paste;
    }

    public void setPaste(String paste) {
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

//    public Boolean getPassProtected() {
//        return isPassProtected;
//    }
//
//    public void setPassProtected(Boolean passProtected) {
//        isPassProtected = passProtected;
//    }
}

