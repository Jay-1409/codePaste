package com.example.codePost.configuration;

import com.example.codePost.entity.Paste;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryConfiguration {
    @Bean
    public Map<String, Paste> pasteTable() {
        return new ConcurrentHashMap<>();
    }

}
