package com.tmc.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Loads the token signing key from identity.token.secret (base64), or falls back to a random one. */
@Configuration
public class TokenSigningKeyConfig {
    private static final Logger log = LoggerFactory.getLogger(TokenSigningKeyConfig.class);

    @Bean
    TokenSigningKey tokenSigningKey(@Value("${identity.token.secret:}") String secret) {
        if (secret.isBlank()) {
            log.warn("identity.token.secret is not set; using a random key. Issued QR codes will stop working after a restart.");
            return TokenSigningKey.random();
        }
        return TokenSigningKey.fromBase64(secret);
    }
}
