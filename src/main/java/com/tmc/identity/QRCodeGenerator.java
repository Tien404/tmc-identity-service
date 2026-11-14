package com.tmc.identity;

import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class QRCodeGenerator {
    private static final Duration TOKEN_LIFETIME = Duration.ofSeconds(60);

    private final EmployeeRepository employees;
    private final TokenCodec tokens;

    public QRCodeGenerator(EmployeeRepository employees, TokenCodec tokens) {
        this.employees = employees;
        this.tokens = tokens;
    }

    public QRCode generateQRCode(int employeeId) {
        var employee = findActiveEmployee(employeeId);
        var expiresAt = calculateExpiry();
        return new QRCode(tokens.issue(employee, expiresAt), expiresAt);
    }

    private Employee findActiveEmployee(int employeeId) {
        var employee = employees.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        if (!employee.active()) throw new IllegalArgumentException("Employee is inactive");
        return employee;
    }

    private Instant calculateExpiry() {
        return Instant.now().plus(TOKEN_LIFETIME);
    }
}
