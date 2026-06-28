package com.example.codePost.service.impl;

import com.example.codePost.service.UidGenerator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Crypto implements UidGenerator {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private boolean fitForConversion(Long number) {
        if (number == null || number < 0) {
            return false;
        }
        if (number == 0) {
            return true;
        }
        int mostSignificantBitPosition = 63 - Long.numberOfLeadingZeros(number);
        return mostSignificantBitPosition < ALPHABET.length();
    }
    private String convert (Long number) {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < 62; ++i) {
            if(((number >> i) & 1) == 1) {
                char character = ALPHABET.charAt(i);
                s.append(character);
            }
        }
        return s.toString();
    }
    @Override
    public Optional<String> encode (Long number) throws Exception {
        if(fitForConversion(number)) {
            return Optional.of(convert(number));
        }
        throw new Exception("Value must be non-negative with its most significant bit below position 62");
    }
    @Override
    public Long decode(String shortid) {
        if (shortid == null) {
            throw new IllegalArgumentException("Short ID cannot be null");
        }
        long value = 0L;
        for (int i = 0; i < shortid.length(); ++i) {
            int bitPosition = ALPHABET.indexOf(shortid.charAt(i));
            if (bitPosition == -1) {
                throw new IllegalArgumentException(
                        "Invalid character in short ID: " + shortid.charAt(i)
                );
            }
            value |= 1L << bitPosition;
        }
        return value;
    }
}
