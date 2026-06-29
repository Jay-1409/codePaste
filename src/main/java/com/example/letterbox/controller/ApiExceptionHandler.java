/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.controller;

import com.example.letterbox.exception.PasteAccessDeniedException;
import com.example.letterbox.exception.PasteExpiredException;
import com.example.letterbox.exception.PasteIdConflictException;
import com.example.letterbox.exception.PasteNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PasteExpiredException.class)
    public ResponseEntity<String> handleExpiredPaste(PasteExpiredException exception) {
        return ResponseEntity.status(HttpStatus.GONE).body(exception.getMessage());
    }

    @ExceptionHandler(PasteNotFoundException.class)
    public ResponseEntity<String> handleMissingPaste(PasteNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(PasteAccessDeniedException.class)
    public ResponseEntity<String> handleDeniedAccess(PasteAccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @ExceptionHandler({PasteIdConflictException.class, DuplicateKeyException.class})
    public ResponseEntity<String> handleDuplicateId(Exception exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String field = error instanceof FieldError fieldError ? fieldError.getField() : "request";
            if (field.equals("passwordConfigurationValid")) {
                field = "pastePass";
            }
            errors.putIfAbsent(field, error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
