package com.tmc.identity;

import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class QRCodeValidator {
    private final EmployeeLookup employees;
    private final TokenReader tokens;
    private final UsedTokenStore usedTokens;

    public QRCodeValidator(EmployeeLookup employees, TokenReader tokens, UsedTokenStore usedTokens) {
        this.employees = employees;
        this.tokens = tokens;
        this.usedTokens = usedTokens;
    }

    public ValidationResult validate(String token, int factoryId) {
        TokenReader.Credential credential;
        try { credential = tokens.read(token); }
        catch (IllegalArgumentException e) { return ValidationResult.denied("Invalid QR code"); }
        if (!credential.expiresAt().isAfter(Instant.now())) return ValidationResult.denied("QR code has expired");
        var employee = employees.findById(credential.employeeId()).orElse(null);
        if (employee == null || !employee.active()) return ValidationResult.denied("Employee is inactive");
        if (employee.factoryId() != factoryId || credential.factoryId() != factoryId)
            return ValidationResult.denied("Employee is not allowed at this factory");
        if (!usedTokens.claim(credential.tokenId())) return ValidationResult.denied("QR code has already been used");
        return new ValidationResult(true, employee.id(), employee.name(), "Verification successful");
    }

    public record ValidationResult(boolean allowed, Integer employeeId, String employeeName, String message) {
        static ValidationResult denied(String message) { return new ValidationResult(false, null, null, message); }
    }
}
