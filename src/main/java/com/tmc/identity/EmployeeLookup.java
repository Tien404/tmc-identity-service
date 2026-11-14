package com.tmc.identity;

import java.util.Optional;

public interface EmployeeLookup {
    Optional<Employee> findById(int id);
}
