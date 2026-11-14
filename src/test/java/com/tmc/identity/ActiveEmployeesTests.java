package com.tmc.identity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActiveEmployeesTests {
    private final ActiveEmployees activeEmployees = new ActiveEmployees(new DemoEmployeeRepository());

    @Test void findReturnsOnlyActiveEmployees() {
        assertTrue(activeEmployees.find(1).isPresent());
        assertTrue(activeEmployees.find(3).isEmpty());   // inactive
        assertTrue(activeEmployees.find(99).isEmpty());  // missing
    }

    @Test void requireExplainsWhy() {
        assertEquals(1, activeEmployees.require(1).id());
        assertEquals("Employee is inactive",
            assertThrows(IllegalArgumentException.class, () -> activeEmployees.require(3)).getMessage());
        assertEquals("Employee not found",
            assertThrows(IllegalArgumentException.class, () -> activeEmployees.require(99)).getMessage());
    }
}
