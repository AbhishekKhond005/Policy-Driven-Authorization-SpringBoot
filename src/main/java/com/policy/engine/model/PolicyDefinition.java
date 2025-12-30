package com.policy.engine.model;

import java.util.List;

public class PolicyDefinition {
    private List<PolicyRule> rules;

    public List<PolicyRule> getRules() {
        return rules;
    }

    public void setRules(List<PolicyRule> rules) {
        this.rules = rules;
    }
}
