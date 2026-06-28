package com.example.codePost.service;

import java.util.Optional;

public interface UidCodec {
    Optional<String> encode(Long number) throws Exception;
    Long decode(String shortId);
}
