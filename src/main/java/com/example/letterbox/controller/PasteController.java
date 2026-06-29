/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.controller;

import com.example.letterbox.entity.Paste;
import com.example.letterbox.entity.PasteBody;
import com.example.letterbox.exception.PasteIdConflictException;
import com.example.letterbox.service.PasteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paste")
public class PasteController {
    private final PasteService pasteService;

    public PasteController(PasteService pasteService) {
        this.pasteService = pasteService;
    }

    @GetMapping("/system")
    public ResponseEntity<String> systemCheck() {
        return ResponseEntity.ok("system is healthy");
    }

    @PostMapping("/addPaste")
    public ResponseEntity<Paste> addPaste(@Valid @RequestBody PasteBody newPaste) {
        String requestedId = newPaste.getPasteId();
        if (requestedId != null && !requestedId.isBlank() && pasteService.checkIfIdExists(requestedId)) {
            throw new PasteIdConflictException(requestedId);
        }
        return ResponseEntity.ok(pasteService.addPaste(newPaste));
    }

    @GetMapping("/pasteExist")
    public ResponseEntity<Boolean> checkIdUniqueness(@RequestParam String pasteId) {
        return ResponseEntity.ok(pasteService.checkIfIdExists(pasteId));
    }

    @GetMapping("/getPaste")
    public ResponseEntity<Paste> getPaste(
            @RequestParam String pasteId,
            @RequestParam(required = false, defaultValue = "") String password
    ) {
        return ResponseEntity.ok(pasteService.getPaste(pasteId, password));
    }

    @DeleteMapping("/deletePaste")
    public ResponseEntity<Boolean> deletePaste(@RequestParam String pasteId) {
        return ResponseEntity.ok(pasteService.deletePaste(pasteId));
    }

    @GetMapping("/protectedPaste")
    public ResponseEntity<Boolean> isThePasteProtected(@RequestParam String pasteId) {
        return ResponseEntity.ok(pasteService.checkIfPassProtected(pasteId));
    }
}
