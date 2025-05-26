package com.example.codePost.repository;

import com.example.codePost.entity.Paste;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasteRepository extends MongoRepository<Paste, String> {
}
