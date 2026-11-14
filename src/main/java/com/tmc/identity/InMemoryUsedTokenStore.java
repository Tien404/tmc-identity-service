package com.tmc.identity;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUsedTokenStore implements UsedTokenStore {
    private final Set<String> usedTokens = ConcurrentHashMap.newKeySet();

    @Override
    public boolean markUsed(String tokenId) {
        return usedTokens.add(tokenId);
    }
}
