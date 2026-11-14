package com.tmc.identity;

import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class QRCodeGenerator {
    private final EmployeeRepository employees;
    private final TokenCodec tokens;

    public QRCodeGenerator(EmployeeRepository employees, TokenCodec tokens) {
        this.employees = employees;
        this.tokens = tokens;
    }

    public QRCode generateQRCode(int employeeId) {
        var employee = employees.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        if (!employee.active()) throw new IllegalArgumentException("Employee is inactive");
        var expiresAt = Instant.now().plusSeconds(60);
        return new QRCode(tokens.issue(employee, expiresAt), expiresAt);
    }
}
