package com.example.letterbox.repository;

import com.example.letterbox.entity.Paste;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasteRepository extends MongoRepository<Paste, String> {
}
