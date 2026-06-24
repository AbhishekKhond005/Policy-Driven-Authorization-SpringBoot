package com.policy.admin.service;

import com.policy.admin.model.PolicyDocument;
import com.policy.engine.core.PolicyEngine;
import com.policy.engine.model.AccessRequest;
import com.policy.engine.model.PolicyDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PolicyService {
    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);
    private final Map<String, PolicyDocument> policies = new ConcurrentHashMap<>();

    public PolicyDocument createPolicy(PolicyDocument document) {
        String id = UUID.randomUUID().toString();
        document.setId(id);
        document.setCreatedAt(System.currentTimeMillis());
        document.setUpdatedAt(System.currentTimeMillis());
        policies.put(id, document);
        log.info("Policy created: {} ({})", document.getName(), id);
        return document;
    }

    public PolicyDocument getPolicy(String id) { return policies.get(id); }

    public List<PolicyDocument> getAllPolicies() { return new ArrayList<>(policies.values()); }

    public PolicyDocument updatePolicy(String id, PolicyDocument document) {
        PolicyDocument existing = policies.get(id);
        if (existing == null) return null;
        document.setId(id);
        document.setCreatedAt(existing.getCreatedAt());
        document.setUpdatedAt(System.currentTimeMillis());
        policies.put(id, document);
        return document;
    }

    public boolean deletePolicy(String id) { return policies.remove(id) != null; }

    public Map<String, Object> simulatePolicy(PolicyDefinition definition, AccessRequest request) {
        PolicyEngine engine = new PolicyEngine(definition);
        boolean granted = policyEngine.evaluate(request);
        Map<String, Object> result = new HashMap<>();
        result.put("granted", granted);
        result.put("matchedRules", engine.getSortedRules().stream()
                .filter(r -> r.getResource() != null)
                .map(r -> Map.of("name", r.getName(), "effect", r.getEffect().name(), "priority", r.getPriority()))
                .toList());
        return result;
    }
}
