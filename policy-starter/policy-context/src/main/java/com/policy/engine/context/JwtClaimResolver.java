package com.policy.engine.context;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtClaimResolver {

    private final ObjectMapper objectMapper;

    public JwtClaimResolver() {
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> resolveClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return Collections.emptyMap();
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            return objectMapper.readValue(payload, Map.class);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public String extractClaim(String token, String claimName) {
        Map<String, Object> claims = resolveClaims(token);
        Object value = claims.get(claimName);
        return value != null ? value.toString() : null;
    }
}
