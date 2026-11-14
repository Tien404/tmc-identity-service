package com.tmc.identity;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TokenCodec {
    private final byte[] key = new byte[32];

    public TokenCodec() { new SecureRandom().nextBytes(key); }

    public String issue(Employee employee, Instant expiresAt) {
        try {
            var claims = new JWTClaimsSet.Builder()
                .issuer("tmc-identity").subject(Integer.toString(employee.id()))
                .claim("factoryId", employee.factoryId()).jwtID(UUID.randomUUID().toString())
                .issueTime(new Date()).expirationTime(Date.from(expiresAt)).build();
            var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(key));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Unable to generate QR code", e);
        }
    }

    public Credential read(String token) {
        try {
            var jwt = SignedJWT.parse(token);
            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm()) || !jwt.verify(new MACVerifier(key))) {
                throw new IllegalArgumentException("Invalid QR code");
            }
            var c = jwt.getJWTClaimsSet();
            if (!"tmc-identity".equals(c.getIssuer()) || c.getJWTID() == null) {
                throw new IllegalArgumentException("Invalid QR code");
            }
            return new Credential(Integer.parseInt(c.getSubject()), c.getIntegerClaim("factoryId"),
                c.getJWTID(), c.getExpirationTime().toInstant());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid QR code");
        }
    }

    public record Credential(int employeeId, int factoryId, String tokenId, Instant expiresAt) {}
}
