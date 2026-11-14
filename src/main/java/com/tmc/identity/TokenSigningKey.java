package com.tmc.identity;

import java.security.SecureRandom;
import java.util.Base64;

/** The secret used to sign and verify QR tokens (HS256, at least 256 bits). */
public final class TokenSigningKey {
    private static final int MIN_BYTES = 32;

    private final byte[] bytes;

    private TokenSigningKey(byte[] bytes) {
        if (bytes.length < MIN_BYTES) {
            throw new IllegalArgumentException("Token signing key must be at least " + MIN_BYTES + " bytes");
        }
        this.bytes = bytes.clone();
    }

    /** A fresh key. Tokens signed with it stop working when the service restarts. */
    public static TokenSigningKey random() {
        var bytes = new byte[MIN_BYTES];
        new SecureRandom().nextBytes(bytes);
        return new TokenSigningKey(bytes);
    }

    public static TokenSigningKey fromBase64(String encoded) {
        return new TokenSigningKey(Base64.getDecoder().decode(encoded.trim()));
    }

    byte[] bytes() {
        return bytes.clone();
    }
}
