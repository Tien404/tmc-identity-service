package com.tmc.identity;

public interface TokenReader {
    Credential read(String token);
}
