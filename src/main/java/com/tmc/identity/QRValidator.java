package com.tmc.identity;

/** Strategy for deciding whether a QR token grants access at a factory. */
public interface QRValidator {
    ValidationResult validate(String token, int factoryId);
}
