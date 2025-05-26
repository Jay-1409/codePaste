package com.example.codePost.controller;

import com.example.codePost.entity.PasteBody;
import com.example.codePost.service.PasteService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/addPaste")
    public ResponseEntity<?> addPaste(@RequestBody PasteBody newPaste) {
        try {
           return new ResponseEntity<>(pasteService.addPaste(newPaste), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getPaste/{pasteId}")
    public ResponseEntity<?> checkIdUniqueness(@RequestParam String pasteId) {
        try {
            return new ResponseEntity<>(pasteService.checkIfIdExists(pasteId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/deletePaste/{pasteId}")
    public ResponseEntity<?> deletePaste(@RequestParam String pasteId) {
        try {
            return new ResponseEntity<>(pasteService.deletePaste(pasteId), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
