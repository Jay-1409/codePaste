package com.example.letterbox.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PasteBody {
    @Size(max = 64, message = "Paste ID must not exceed 64 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "Paste ID may only contain letters, numbers, underscores, and hyphens")
    private String pasteId = "";

    @NotBlank(message = "Paste content is required")
    @Size(max = 524_288, message = "Paste content must not exceed 524,288 characters")
    private String paste = "";

    @Positive(message = "Expiration must be greater than zero days")
    @Max(value = 365, message = "Expiration must not exceed 365 days")
    private Long expireAfter = null;

    @NotNull(message = "Access mode is required")
    private Boolean access = true;

    @Size(min = 8, max = 72, message = "Password must contain between 8 and 72 characters")
    private String pastePass = null;

    @AssertTrue(message = "A password is required for a protected paste")
    @JsonIgnore
    public boolean isPasswordConfigurationValid() {
        return !Boolean.FALSE.equals(access) || (pastePass != null && !pastePass.isBlank());
    }

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

}
