package com.tmc.identity;

import java.util.Optional;
import org.springframework.stereotype.Component;

/** The single place that decides which employees may get or use a QR code. */
@Component
public class ActiveEmployees {
    private final EmployeeRepository employees;

    public ActiveEmployees(EmployeeRepository employees) {
        this.employees = employees;
    }

    /** Returns the employee if they exist and are active; empty otherwise. */
    public Optional<Employee> find(int employeeId) {
        return employees.findById(employeeId).filter(ActiveEmployees::isActive);
    }

    /** Returns the employee, or throws with the reason they can't have a QR code. */
    public Employee require(int employeeId) {
        var employee = employees.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        if (!isActive(employee)) throw new IllegalArgumentException("Employee is inactive");
        return employee;
    }

    private static boolean isActive(Employee employee) {
        return employee.active();
    }
}
