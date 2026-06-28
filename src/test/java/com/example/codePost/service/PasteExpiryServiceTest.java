package com.example.codePost.service;

import com.example.codePost.entity.Paste;
import com.example.codePost.exception.PasteExpiredException;
import com.example.codePost.repository.PasteRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PasteExpiryServiceTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-06-28T10:00:00Z"),
            ZoneOffset.UTC
    );

    @Test
    void deletesAndEvictsExpiredPaste() {
        PasteRepository repository = mock(PasteRepository.class);
        PasteCache cache = mock(PasteCache.class);
        PasteExpiryService service = new PasteExpiryService(repository, cache, CLOCK);
        Paste paste = new Paste();
        paste.setPasteId("expired-id");
        paste.setExpireAfter(CLOCK.instant().minusSeconds(1));

        assertThrows(PasteExpiredException.class, () -> service.requireActive(paste));
        verify(repository).deleteById("expired-id");
        verify(cache).evict("expired-id");
    }

    @Test
    void returnsActivePaste() {
        PasteRepository repository = mock(PasteRepository.class);
        PasteCache cache = mock(PasteCache.class);
        PasteExpiryService service = new PasteExpiryService(repository, cache, CLOCK);
        Paste paste = new Paste();
        paste.setExpireAfter(CLOCK.instant().plusSeconds(1));

        assertEquals(paste, service.requireActive(paste));
    }
}
