package com.example.codePost.service;

import com.example.codePost.entity.Paste;
import com.example.codePost.entity.PasteBody;
import com.example.codePost.repository.PasteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.annotation.NonNull;
import scala.collection.mutable.HashTable;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class PasteService {
    @Autowired
    PasteRepository pasteRepository;
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    @Transactional
    public Paste addPaste(@NonNull PasteBody paste) {
        Paste newPaste = new Paste();
        String dumId = NanoIdUtils.randomNanoId();
        newPaste.setPasteId( paste.getPasteId() == null ? dumId : paste.getPasteId());
        newPaste.setPaste(paste.getPaste());
        if(paste.getPassProtected()) {
            newPaste.setPastePass(encoder.encode(paste.getPastePass()));
            newPaste.setPassProtected(true);
        }
        Instant expiration = paste.getExpireAfter() == null ? Instant.now().plus(Duration.ofDays(30)) : Instant.now().plus(Duration.ofDays(paste.getExpireAfter()));
        newPaste.setExpireAfter(expiration);
        newPaste.setAccess(paste.getAccess());
        pasteRepository.save(newPaste);
        return newPaste;
    }
//    HashTable<String, Paste> pasteTable;

    public boolean checkIfIdExists(String pasteId) {
        Optional<Paste> reqPaste = pasteRepository.findById(pasteId);
        if(reqPaste.isPresent()){
            return true;
        }
        return false;
    }
}
