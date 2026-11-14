package com.tmc.identity;

/** Remembers which tokens have been used, so each QR code works only once. */
public interface UsedTokenStore {
    /** Marks the token as used. Returns false if it was already used. */
    boolean markUsed(String tokenId);
}
