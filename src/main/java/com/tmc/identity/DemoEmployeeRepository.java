package com.tmc.identity;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class DemoEmployeeRepository implements EmployeeRepository {
    private final List<Employee> employees = List.of(
        new Employee(1, "Alex Morgan", 1, true),
        new Employee(2, "Jordan Lee", 2, true),
        new Employee(3, "Casey Taylor", 1, false)
    );

    public List<Employee> findAll() { return employees; }
    public Optional<Employee> findById(int id) {
        return employees.stream().filter(e -> e.id() == id).findFirst();
    }
}
