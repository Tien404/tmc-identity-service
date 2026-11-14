package com.tmc.identity;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class QRCodeValidator {
    private final EmployeeRepository employees;
    private final TokenCodec tokens;
    private final Set<String> usedTokens = ConcurrentHashMap.newKeySet();

    public QRCodeValidator(EmployeeRepository employees, TokenCodec tokens) {
        this.employees = employees;
        this.tokens = tokens;
    }

    public ValidationResult validate(String token, int factoryId) {
        TokenCodec.Credential credential;
        try { credential = tokens.read(token); }
        catch (IllegalArgumentException e) { return ValidationResult.denied("Invalid QR code"); }
        if (!credential.expiresAt().isAfter(Instant.now())) return ValidationResult.denied("QR code has expired");
        var employee = employees.findById(credential.employeeId()).orElse(null);
        if (employee == null || !employee.active()) return ValidationResult.denied("Employee is inactive");
        if (employee.factoryId() != factoryId || credential.factoryId() != factoryId)
            return ValidationResult.denied("Employee is not allowed at this factory");
        if (!usedTokens.add(credential.tokenId())) return ValidationResult.denied("QR code has already been used");
        return new ValidationResult(true, employee.id(), employee.name(), "Verification successful");
    }

    public record ValidationResult(boolean allowed, Integer employeeId, String employeeName, String message) {
        static ValidationResult denied(String message) { return new ValidationResult(false, null, null, message); }
    }
}
