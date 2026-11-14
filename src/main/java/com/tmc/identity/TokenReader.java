package com.tmc.identity;

import java.time.Instant;

public interface TokenReader {
    /** @throws IllegalArgumentException if the signed token cannot be verified */
    Credential read(String token);

    record Credential(int employeeId, int factoryId, String tokenId, Instant expiresAt) {}
}
