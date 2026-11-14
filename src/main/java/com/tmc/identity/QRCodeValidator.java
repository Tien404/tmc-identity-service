package com.tmc.identity;

import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class QRCodeValidator {
    private final ActiveEmployees activeEmployees;
    private final TokenCodec tokens;
    private final UsedTokenStore usedTokens;

    public QRCodeValidator(ActiveEmployees activeEmployees, TokenCodec tokens, UsedTokenStore usedTokens) {
        this.activeEmployees = activeEmployees;
        this.tokens = tokens;
        this.usedTokens = usedTokens;
    }

    public ValidationResult validate(String token, int factoryId) {
        var credential = readCredential(token).orElse(null);
        if (credential == null) return ValidationResult.denied("Invalid QR code");
        if (isExpired(credential)) return ValidationResult.denied("QR code has expired");

        var employee = activeEmployees.find(credential.employeeId()).orElse(null);
        if (employee == null) return ValidationResult.denied("Employee is inactive");
        if (!isAllowedAtFactory(employee, credential, factoryId))
            return ValidationResult.denied("Employee is not allowed at this factory");

        if (!usedTokens.markUsed(credential.tokenId())) return ValidationResult.denied("QR code has already been used");
        return ValidationResult.allowed(employee);
    }

    private Optional<TokenCodec.Credential> readCredential(String token) {
        try {
            return Optional.of(tokens.read(token));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private boolean isExpired(TokenCodec.Credential credential) {
        return !credential.expiresAt().isAfter(Instant.now());
    }

    private boolean isAllowedAtFactory(Employee employee, TokenCodec.Credential credential, int factoryId) {
        return employee.factoryId() == factoryId && credential.factoryId() == factoryId;
    }
}
