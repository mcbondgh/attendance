package com.cass.security;

import java.util.Base64;

public class Encryption {
    public static String generateCipherText(String userInput) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(userInput.getBytes());
    }

    public static String getOriginalText(String hashCode) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] hashedByte = decoder.decode(hashCode);
        return new String(hashedByte);
    }
}
