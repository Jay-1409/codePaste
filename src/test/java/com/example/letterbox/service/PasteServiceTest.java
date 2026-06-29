package com.example.letterbox.service;

import com.example.letterbox.entity.Paste;
import com.example.letterbox.entity.PasteBody;
import com.example.letterbox.repository.PasteRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PasteServiceTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-06-28T10:00:00Z"),
            ZoneOffset.UTC
    );

    @Test
    void generatesIdWhenPasteIdIsBlank() {
        PasteRepository repository = mock(PasteRepository.class);
        PasteIdGenerator idGenerator = mock(PasteIdGenerator.class);
        PasteCache cache = mock(PasteCache.class);
        PasteExpiryPolicy expiryService = mock(PasteExpiryPolicy.class);
        PasteAccessPolicy accessService = mock(PasteAccessPolicy.class);
        PasteService service = new PasteService(
                repository, idGenerator, cache, expiryService, accessService, CLOCK
        );
        PasteBody body = new PasteBody();
        body.setPaste("content");

        when(idGenerator.nextId()).thenReturn("generated-id");
        when(repository.insert(any(Paste.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Paste savedPaste = service.addPaste(body);

        assertEquals("generated-id", savedPaste.getPasteId());
        assertEquals(Instant.parse("2026-07-28T10:00:00Z"), savedPaste.getExpireAfter());
        verify(idGenerator).nextId();
        verify(repository).insert(savedPaste);
        verify(cache).put(savedPaste);
    }

    @Test
    void preservesCustomIdWithoutUsingGenerator() {
        PasteRepository repository = mock(PasteRepository.class);
        PasteIdGenerator idGenerator = mock(PasteIdGenerator.class);
        PasteCache cache = mock(PasteCache.class);
        PasteExpiryPolicy expiryService = mock(PasteExpiryPolicy.class);
        PasteAccessPolicy accessService = mock(PasteAccessPolicy.class);
        PasteService service = new PasteService(
                repository, idGenerator, cache, expiryService, accessService, CLOCK
        );
        PasteBody body = new PasteBody();
        body.setPasteId("custom-id");
        body.setPaste("content");

        when(repository.insert(any(Paste.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Paste savedPaste = service.addPaste(body);

        assertEquals("custom-id", savedPaste.getPasteId());
        verifyNoInteractions(idGenerator);
        verify(repository).insert(savedPaste);
    }

    @Test
    void delegatesExpiryAndAccessChecksWhenFetching() {
        PasteRepository repository = mock(PasteRepository.class);
        PasteIdGenerator idGenerator = mock(PasteIdGenerator.class);
        PasteCache cache = mock(PasteCache.class);
        PasteExpiryPolicy expiryService = mock(PasteExpiryPolicy.class);
        PasteAccessPolicy accessService = mock(PasteAccessPolicy.class);
        PasteService service = new PasteService(
                repository, idGenerator, cache, expiryService, accessService, CLOCK
        );
        Paste activePaste = new Paste();
        activePaste.setPasteId("active-id");

        when(cache.findById("active-id")).thenReturn(activePaste);
        when(expiryService.requireActive(activePaste)).thenReturn(activePaste);

        assertEquals(activePaste, service.getPaste("active-id", "password"));
        verify(expiryService).requireActive(activePaste);
        verify(accessService).verifyAccess(activePaste, "password");
    }
}
