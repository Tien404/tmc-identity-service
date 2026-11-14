package com.tmc.identity;

import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class QRCodeGenerator {
    private static final Duration TOKEN_LIFETIME = Duration.ofSeconds(60);

    private final ActiveEmployees activeEmployees;
    private final TokenCodec tokens;

    public QRCodeGenerator(ActiveEmployees activeEmployees, TokenCodec tokens) {
        this.activeEmployees = activeEmployees;
        this.tokens = tokens;
    }

    public QRCode generateQRCode(int employeeId) {
        var employee = activeEmployees.require(employeeId);
        var expiresAt = calculateExpiry();
        return new QRCode(tokens.issue(employee, expiresAt), expiresAt);
    }

    private Instant calculateExpiry() {
        return Instant.now().plus(TOKEN_LIFETIME);
    }
}
