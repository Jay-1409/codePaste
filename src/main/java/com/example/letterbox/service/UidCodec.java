/*
 *
 *  * Copyright (c) 2025-2026 Jay Shah
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.example.letterbox.service;

import java.util.Optional;

public interface UidCodec {
    Optional<String> encode(Long number) throws Exception;
    Long decode(String shortId);
}
