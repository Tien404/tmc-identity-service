package com.tmc.identity;

public interface UsedTokenStore {
    /** Atomically claims a token ID. Returns false if it has already been claimed. */
    boolean claim(String tokenId);
}
