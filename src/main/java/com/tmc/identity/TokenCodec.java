package com.tmc.identity;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class TokenCodec implements TokenIssuer, TokenReader {
    private static final String ISSUER = "tmc-identity";

    private final SecretKey key;

    public TokenCodec() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        key = new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Override
    public String issue(Employee employee, Instant expiresAt) {
        try {
            var claims = new JWTClaimsSet.Builder()
                .issuer(ISSUER).subject(Integer.toString(employee.id()))
                .claim("factoryId", employee.factoryId()).jwtID(UUID.randomUUID().toString())
                .issueTime(new Date()).expirationTime(Date.from(expiresAt)).build();
            var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(key));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Unable to generate QR code", e);
        }
    }

    @Override
    public Credential read(String token) {
        try {
            var jwt = SignedJWT.parse(token);
            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm()) || !jwt.verify(new MACVerifier(key))) {
                throw new IllegalArgumentException("Invalid QR code");
            }
            var c = jwt.getJWTClaimsSet();
            if (!ISSUER.equals(c.getIssuer()) || c.getJWTID() == null) {
                throw new IllegalArgumentException("Invalid QR code");
            }
            return new Credential(Integer.parseInt(c.getSubject()), c.getIntegerClaim("factoryId"),
                c.getJWTID(), c.getExpirationTime().toInstant());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid QR code");
        }
    }
}
