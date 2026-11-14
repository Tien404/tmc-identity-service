package com.tmc.identity;

import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** Online validator: checks the token against live employee data and the used-token store. */
@Service
public class DynamicValidator implements QRValidator {
    private final EmployeeRepository employees;
    private final TokenCodec tokens;
    private final UsedTokenStore usedTokens;

    public DynamicValidator(EmployeeRepository employees, TokenCodec tokens, UsedTokenStore usedTokens) {
        this.employees = employees;
        this.tokens = tokens;
        this.usedTokens = usedTokens;
    }

    @Override
    public ValidationResult validate(String token, int factoryId) {
        var credential = readCredential(token).orElse(null);
        if (credential == null) return ValidationResult.denied("Invalid QR code");
        if (isExpired(credential)) return ValidationResult.denied("QR code has expired");

        var employee = findActiveEmployee(credential.employeeId()).orElse(null);
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

    private Optional<Employee> findActiveEmployee(int employeeId) {
        return employees.findById(employeeId).filter(Employee::active);
    }

    private boolean isAllowedAtFactory(Employee employee, TokenCodec.Credential credential, int factoryId) {
        return employee.factoryId() == factoryId && credential.factoryId() == factoryId;
    }
}
