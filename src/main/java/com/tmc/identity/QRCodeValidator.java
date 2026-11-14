package com.tmc.identity;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class QRCodeValidator {
    private final EmployeeRepository employees;
    private final TokenReader tokenReader;
    private final Set<String> usedTokens = ConcurrentHashMap.newKeySet();

    public QRCodeValidator(EmployeeRepository employees, TokenReader tokenReader) {
        this.employees = employees;
        this.tokenReader = tokenReader;
    }

    public ValidationResult validate(String token, int factoryId) {
        var credential = readCredential(token).orElse(null);
        if (credential == null) return ValidationResult.denied("Invalid QR code");
        if (isExpired(credential)) return ValidationResult.denied("QR code has expired");

        var employee = findActiveEmployee(credential.employeeId()).orElse(null);
        if (employee == null) return ValidationResult.denied("Employee is inactive");
        if (!isAllowedAtFactory(employee, credential, factoryId))
            return ValidationResult.denied("Employee is not allowed at this factory");

        if (!usedTokens.add(credential.tokenId())) return ValidationResult.denied("QR code has already been used");
        return ValidationResult.allowed(employee);
    }

    private Optional<Credential> readCredential(String token) {
        try {
            return Optional.of(tokenReader.read(token));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private boolean isExpired(Credential credential) {
        return !credential.expiresAt().isAfter(Instant.now());
    }

    private Optional<Employee> findActiveEmployee(int employeeId) {
        return employees.findById(employeeId).filter(Employee::active);
    }

    private boolean isAllowedAtFactory(Employee employee, Credential credential, int factoryId) {
        return employee.factoryId() == factoryId && credential.factoryId() == factoryId;
    }

    public record ValidationResult(boolean allowed, Integer employeeId, String employeeName, String message) {
        static ValidationResult allowed(Employee employee) {
            return new ValidationResult(true, employee.id(), employee.name(), "Verification successful");
        }

        static ValidationResult denied(String message) {
            return new ValidationResult(false, null, null, message);
        }
    }
}
