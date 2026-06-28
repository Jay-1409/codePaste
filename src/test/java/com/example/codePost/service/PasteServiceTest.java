package com.example.codePost.service;

import com.example.codePost.entity.Paste;
import com.example.codePost.entity.PasteBody;
import com.example.codePost.repository.PasteRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PasteServiceTest {

    @Test
    void generatesIdWhenPasteIdIsBlank() throws InterruptedException {
        PasteRepository repository = mock(PasteRepository.class);
        UidPool uidPool = mock(UidPool.class);
        PasteService service = new PasteService(repository, uidPool);
        PasteBody body = new PasteBody();
        body.setPaste("content");

        when(uidPool.GetId()).thenReturn("generated-id");
        when(repository.save(any(Paste.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Paste savedPaste = service.addPaste(body);

        assertEquals("generated-id", savedPaste.getPasteId());
        verify(uidPool).GetId();
        verify(repository).save(savedPaste);
    }

    @Test
    void preservesCustomIdWithoutUsingPool() throws InterruptedException {
        PasteRepository repository = mock(PasteRepository.class);
        UidPool uidPool = mock(UidPool.class);
        PasteService service = new PasteService(repository, uidPool);
        PasteBody body = new PasteBody();
        body.setPasteId("custom-id");
        body.setPaste("content");

        when(repository.save(any(Paste.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Paste savedPaste = service.addPaste(body);

        assertEquals("custom-id", savedPaste.getPasteId());
        verifyNoInteractions(uidPool);
        verify(repository).save(savedPaste);
    }
}
