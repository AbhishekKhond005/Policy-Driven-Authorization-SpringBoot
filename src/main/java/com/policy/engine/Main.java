package com.policy.engine;

import com.policy.engine.core.PolicyEngine;
import com.policy.engine.loader.JsonPolicyLoader;
import com.policy.engine.model.AccessRequest;
import com.policy.engine.model.PolicyDefinition;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // 1. Load Policy
        // Note: For simple java main, we use full path or relative path
        JsonPolicyLoader loader = new JsonPolicyLoader("src/main/resources/policy.json");
        PolicyDefinition definition = loader.load();

        // 2. Init Engine
        PolicyEngine engine = new PolicyEngine(definition);

        // 3. Test 1: Admin
        Map<String, Object> adminContext = new HashMap<>();
        adminContext.put("role", "ADMIN");
        AccessRequest request1 = new AccessRequest("Bob", "/api/admin/123", "GET", adminContext);
        System.out.println("Test 1 (Admin Should Pass): " + engine.evaluate(request1));

        // 4. Test 2: Dynamic
        Map<String, Object> dynamicContext = new HashMap<>();
        dynamicContext.put("resource_owner", "Bob");
        dynamicContext.put("current_user", "Bob");
        AccessRequest request2 = new AccessRequest("Bob", "/api/profile/1", "GET", dynamicContext);
        System.out.println("Test 2 (Owner Should Pass): " + engine.evaluate(request2));

        // 5. Test 3: Failure
        Map<String, Object> failContext = new HashMap<>();
        failContext.put("role", "USER");
        AccessRequest request3 = new AccessRequest("Bob", "/api/admin/123", "GET", failContext);
        System.out.println("Test 3 (User Should Fail): " + engine.evaluate(request3));
    }
}