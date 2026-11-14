package com.tmc.identity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class IdentityController {
    private final EmployeeRepository employees;
    private final QRCodeGenerator generator;
    private final QRCodeValidator validator;

    public IdentityController(EmployeeRepository employees, QRCodeGenerator generator, QRCodeValidator validator) {
        this.employees = employees;
        this.generator = generator;
        this.validator = validator;
    }

    @GetMapping("/employees")
    public List<Employee> employees() { return employees.findAll(); }

    @PostMapping("/employees/{id}/qr")
    public QRCode generate(@PathVariable int id) { return generator.generateQRCode(id); }

    @PostMapping("/validation")
    public ValidationResult validate(@Valid @RequestBody ValidationRequest request) {
        return validator.validate(request.token(), request.factoryId());
    }

    public record ValidationRequest(@NotBlank String token, @Positive int factoryId) {}
}
