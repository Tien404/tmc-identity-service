package com.tmc.identity;

import java.time.Instant;

public record Credential(int employeeId, int factoryId, String tokenId, Instant expiresAt) {}
