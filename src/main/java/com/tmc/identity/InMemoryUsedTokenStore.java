package com.tmc.identity;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUsedTokenStore implements UsedTokenStore {
    private final Set<String> claimedTokens = ConcurrentHashMap.newKeySet();

    @Override
    public boolean claim(String tokenId) {
        return claimedTokens.add(tokenId);
    }
}
