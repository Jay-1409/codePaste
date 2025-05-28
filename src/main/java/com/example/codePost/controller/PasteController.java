package com.example.codePost.controller;

import com.example.codePost.entity.PasteBody;
import com.example.codePost.service.PasteService;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveFindOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paste")
public class PasteController {
    @Autowired
    PasteService pasteService;

    @GetMapping("/system")
    public ResponseEntity<?> systemCheck() {
        return new ResponseEntity<>("system is healthy", HttpStatus.OK);
    }
    @PostMapping("/addPaste")
    public ResponseEntity<?> addPaste(@RequestBody PasteBody newPaste) {
        if(!newPaste.getPasteId().isEmpty()) {
            if(pasteService.checkIfIdExists(newPaste.getPasteId())){
                return new ResponseEntity<>("This paste id is not avail", HttpStatus.CONFLICT);
            }
        }
        try {
           return new ResponseEntity<>(pasteService.addPaste(newPaste), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getCause(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/pasteExist")
    public ResponseEntity<Boolean> checkIdUniqueness(@RequestParam String pasteId) {
        try {
            return new ResponseEntity<>(pasteService.checkIfIdExists(pasteId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getPaste")
    public ResponseEntity<?> getPaste(@RequestParam String pasteId, @RequestParam String password) {
        try {
            if(pasteService.checkIfIdExists(pasteId)) {
                Boolean getAccess = isThePasteProtected(pasteId).getBody();
                if (Boolean.TRUE.equals(getAccess) && (password == null || password.isEmpty())) {
                    return new ResponseEntity<>("This is a password-protected paste", HttpStatus.FORBIDDEN);
                }
                return new ResponseEntity<>(pasteService.getPaste(pasteId, password), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Id not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/deletePaste")
    public ResponseEntity<?> deletePaste(@RequestParam String pasteId) {
        try {
            return new ResponseEntity<>(pasteService.deletePaste(pasteId), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/protectedPaste")
     public ResponseEntity<Boolean> isThePasteProtected(@RequestParam String pasteId){
        try {
            return new ResponseEntity<Boolean>(pasteService.checkIfPassProtected(pasteId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
    }
}
