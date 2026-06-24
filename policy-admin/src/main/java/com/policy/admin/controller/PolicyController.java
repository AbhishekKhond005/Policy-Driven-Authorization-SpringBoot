package com.policy.admin.controller;

import com.policy.admin.model.PolicyDocument;
import com.policy.admin.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/policies")
public class PolicyController {
    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) { this.policyService = policyService; }

    @PostMapping
    public ResponseEntity<PolicyDocument> create(@RequestBody PolicyDocument document) {
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.createPolicy(document));
    }

    @GetMapping
    public List<PolicyDocument> list() { return policyService.getAllPolicies(); }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyDocument> get(@PathVariable String id) {
        PolicyDocument doc = policyService.getPolicy(id);
        return doc != null ? ResponseEntity.ok(doc) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PolicyDocument> update(@PathVariable String id, @RequestBody PolicyDocument document) {
        PolicyDocument updated = policyService.updatePolicy(id, document);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return policyService.deletePolicy(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
