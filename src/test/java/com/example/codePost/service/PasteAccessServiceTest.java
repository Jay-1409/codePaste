package com.example.codePost.service;

import com.example.codePost.entity.Paste;
import com.example.codePost.exception.PasteAccessDeniedException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PasteAccessServiceTest {

    @Test
    void verifiesProtectedPasteWithPasswordEncoder() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        PasteAccessService service = new PasteAccessService(encoder);
        Paste paste = new Paste();
        paste.setAccess(false);
        paste.setPastePass("hash");
        when(encoder.matches("secret", "hash")).thenReturn(true);

        assertDoesNotThrow(() -> service.verifyAccess(paste, "secret"));
        verify(encoder).matches("secret", "hash");
    }

    @Test
    void rejectsWrongPassword() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        PasteAccessService service = new PasteAccessService(encoder);
        Paste paste = new Paste();
        paste.setAccess(false);
        paste.setPastePass("hash");

        assertThrows(PasteAccessDeniedException.class, () -> service.verifyAccess(paste, "wrong"));
    }
}
