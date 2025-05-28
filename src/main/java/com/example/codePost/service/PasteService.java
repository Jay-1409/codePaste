package com.example.codePost.service;

import com.example.codePost.entity.Paste;
import com.example.codePost.entity.PasteBody;
import com.example.codePost.repository.PasteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class PasteService {
    @Autowired
    PasteRepository pasteRepository;
    PasswordEncoder encoder = new BCryptPasswordEncoder();


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "pasteExistsCache", key = "#result.pasteId" )
    },
        put = {
            @CachePut(value = "pastes", key  = "#result.pasteId")
        }
    )
    public Paste addPaste(@NotNull PasteBody paste) {
        Paste newPaste = new Paste();
        String dumId = NanoIdUtils.randomNanoId();
        String finalId = (paste.getPasteId() == null || paste.getPasteId().isBlank()) ? dumId : paste.getPasteId();
        newPaste.setPasteId(finalId);
        newPaste.setPaste(paste.getPaste());
        if (paste.getAccess().equals(Boolean.FALSE) && paste.getPastePass() != null) {
            newPaste.setPastePass(encoder.encode(paste.getPastePass()));
            newPaste.setPassProtected(true);
        }
        Instant expiration = paste.getExpireAfter() == null ? Instant.now().plus(Duration.ofDays(30)) : Instant.now().plus(Duration.ofDays(paste.getExpireAfter()));
        newPaste.setExpireAfter(expiration);
        newPaste.setAccess(paste.getAccess());
        pasteRepository.save(newPaste);
        return newPaste;
    }
    @Cacheable(value = "pasteExistsCache", key = "#pasteId")
    public boolean checkIfIdExists(String pasteId) {
        Optional<Paste> reqPaste = pasteRepository.findById(pasteId);
        return reqPaste.isPresent();
    }
    @Cacheable(value = "isPassProtected", key = "#pasteId")
    public boolean checkIfPassProtected(String pasteId){
        Optional<Paste> reqPaste = pasteRepository.findById(pasteId);
        if(reqPaste.isPresent()) {
            Paste activePaste = reqPaste.get();
            if(activePaste.getAccess().equals(false)) {
                return true;
            }
        }
        return false;
    }
    @Cacheable(value = "pastes", key = "#pasteId")
    public Paste getPaste(String pasteId, String password){
        Optional<Paste> reqPaste = pasteRepository.findById(pasteId);
        if(reqPaste.isPresent()) {
            Paste activePaste = reqPaste.get();
            if(activePaste.getAccess().equals(false)) {
                if(password.equals(activePaste.getPastePass())){
                    return activePaste;
                }
            }
        }
        return null;
    }
    @Caching(evict = {
            @CacheEvict(value = "pastes", key = "#pasteId"),
            @CacheEvict(value = "pasteExistsCache", key = "#pasteId")
    })
    public Boolean deletePaste(String pasteid){
        try {
            pasteRepository.deleteById(pasteid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
