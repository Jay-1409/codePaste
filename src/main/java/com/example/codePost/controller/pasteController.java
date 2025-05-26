package com.example.codePost.controller;

import com.mongodb.lang.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paste")
public class pasteController {
    @GetMapping("/system")
    public ResponseEntity<?> systemCheck() {
        return new ResponseEntity<>("system is healthy", HttpStatus.OK);
    }
}
