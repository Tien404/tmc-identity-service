package com.tmc.identity;

import org.springframework.stereotype.Service;

/** Entry point for QR validation; delegates to the configured {@link QRValidator} strategy. */
@Service
public class QRCodeValidator {
    private final QRValidator validatorStrategy;

    public QRCodeValidator(QRValidator validatorStrategy) {
        this.validatorStrategy = validatorStrategy;
    }

    public ValidationResult validate(String token, int factoryId) {
        return validatorStrategy.validate(token, factoryId);
    }
}
