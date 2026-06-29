package com.example.letterbox.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasteBodyValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsBlankPasteContent() {
        PasteBody body = new PasteBody();
        body.setPaste("   ");

        assertTrue(fieldsFor(body).contains("paste"));
    }

    @Test
    void rejectsInvalidIdAndExpiration() {
        PasteBody body = new PasteBody();
        body.setPaste("content");
        body.setPasteId("invalid id!");
        body.setExpireAfter(0L);

        Set<String> fields = fieldsFor(body);
        assertTrue(fields.contains("pasteId"));
        assertTrue(fields.contains("expireAfter"));
    }

    @Test
    void requiresPasswordForProtectedPaste() {
        PasteBody body = new PasteBody();
        body.setPaste("content");
        body.setAccess(false);

        assertTrue(fieldsFor(body).contains("passwordConfigurationValid"));
    }

    @Test
    void acceptsValidProtectedPaste() {
        PasteBody body = new PasteBody();
        body.setPaste("content");
        body.setAccess(false);
        body.setPastePass("password");
        body.setExpireAfter(30L);

        assertTrue(validator.validate(body).isEmpty());
    }

    private Set<String> fieldsFor(PasteBody body) {
        return validator.validate(body).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}
