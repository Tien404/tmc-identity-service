package com.tmc.identity;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QRCodeValidatorTests {
    private final EmployeeRepository employees = new DemoEmployeeRepository();
    private final TokenCodec tokens = new SignedTokenCodec(TokenSigningKey.random());
    private final QRCodeValidator validator =
        new QRCodeValidator(new ActiveEmployees(employees), tokens, new InMemoryUsedTokenStore());

    private String token(int id, Instant expiresAt) {
        return tokens.issue(employees.findById(id).orElseThrow(), expiresAt);
    }

    @Test void acceptsValidTokenOnlyOnce() {
        var token = token(1, Instant.now().plusSeconds(60));
        assertTrue(validator.validate(token, 1).allowed());
        assertFalse(validator.validate(token, 1).allowed());
    }

    @Test void rejectsExpiredTamperedAndForeignTokens() {
        assertFalse(validator.validate(token(1, Instant.now().minusSeconds(1)), 1).allowed());
        assertFalse(validator.validate("invalid.token", 1).allowed());
        var foreignToken = new SignedTokenCodec(TokenSigningKey.random())
            .issue(employees.findById(1).orElseThrow(), Instant.now().plusSeconds(60));
        assertFalse(validator.validate(foreignToken, 1).allowed());
    }

    @Test void checksEmployeeStatusAndFactory() {
        assertFalse(validator.validate(token(3, Instant.now().plusSeconds(60)), 1).allowed());
        assertFalse(validator.validate(token(2, Instant.now().plusSeconds(60)), 1).allowed());
    }
}
