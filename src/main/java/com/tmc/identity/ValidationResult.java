package com.tmc.identity;

public record ValidationResult(boolean allowed, Integer employeeId, String employeeName, String message) {
    static ValidationResult allowed(Employee employee) {
        return new ValidationResult(true, employee.id(), employee.name(), "Verification successful");
    }

    static ValidationResult denied(String message) {
        return new ValidationResult(false, null, null, message);
    }
}
