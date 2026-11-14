package com.tmc.identity;

import java.time.Instant;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenSigningKeyTests {
    private final Employee employee = new Employee(1, "Alex Morgan", 1, true);

    @Test void sameConfiguredSecretSurvivesRestart() {
        var secret = Base64.getEncoder().encodeToString(new byte[32]);
        var token = new SignedTokenCodec(TokenSigningKey.fromBase64(secret)).issue(employee, Instant.now().plusSeconds(60));

        var afterRestart = new SignedTokenCodec(TokenSigningKey.fromBase64(secret));
        assertEquals(1, afterRestart.read(token).employeeId());
    }

    @Test void rejectsSecretShorterThan256Bits() {
        var shortSecret = Base64.getEncoder().encodeToString(new byte[16]);
        assertThrows(IllegalArgumentException.class, () -> TokenSigningKey.fromBase64(shortSecret));
    }
}
