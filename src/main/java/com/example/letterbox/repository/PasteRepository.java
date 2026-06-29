/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.repository;

import com.example.letterbox.entity.Paste;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasteRepository extends MongoRepository<Paste, String> {
}
