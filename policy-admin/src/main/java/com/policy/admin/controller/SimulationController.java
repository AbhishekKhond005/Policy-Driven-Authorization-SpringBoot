package com.policy.admin.controller;

import com.policy.admin.service.PolicyService;
import com.policy.engine.model.AccessRequest;
import com.policy.engine.model.PolicyDefinition;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/simulate")
public class SimulationController {
    private final PolicyService policyService;

    public SimulationController(PolicyService policyService) { this.policyService = policyService; }

    @PostMapping
    public Map<String, Object> simulate(@RequestBody SimulationRequest request) {
        return policyService.simulatePolicy(request.policyDefinition, request.accessRequest);
    }

    public record SimulationRequest(PolicyDefinition policyDefinition, AccessRequest accessRequest) {}
}
