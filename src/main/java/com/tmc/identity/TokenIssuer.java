package com.tmc.identity;

import java.time.Instant;

public interface TokenIssuer {
    String issue(Employee employee, Instant expiresAt);
}
