package com.policy.engine.core;

import com.policy.engine.model.PolicyDefinition;
import com.policy.engine.model.PolicyRule;
import com.policy.engine.model.ValueType;
import com.policy.engine.model.Condition;
import com.policy.engine.model.Operator;

import java.util.Set;

import com.policy.engine.model.AccessRequest;

public class PolicyEngine {
    private PolicyDefinition policyDefinition;

    public PolicyEngine(PolicyDefinition policyDefinition) {
        this.policyDefinition = policyDefinition;
    }

    public boolean evaluate(AccessRequest request) {
        String requestedResource = request.getResource();
        String requestedAction = request.getAction();

        for (PolicyRule rule : policyDefinition.getRules()) {
            String policyResource = rule.getResource();
            Set<String> policyActions = rule.getActions();
            boolean matchResource = matchResource(rule.getResource(), requestedResource);
            if (validAction(requestedAction, policyActions) && matchResource) {
                if (evaluateConditions(rule, request)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean compareResources(String resourceOne, String resourceTwo) {
        return resourceOne.equals(resourceTwo);
    }

    private boolean validAction(String action, Set<String> actions) {
        return actions.contains(action);
    }

    private boolean matchResource(String ruleResource, String requestResource) {
        // Simple Logic: if rule ends with "/**", match prefix. Else match exact.
        if (ruleResource.endsWith("/**")) {
            String prefix = ruleResource.substring(0, ruleResource.length() - 3);
            return requestResource.startsWith(prefix);
        }
        return ruleResource.equals(requestResource);
    }

    private boolean evaluateConditions(PolicyRule rule, AccessRequest request) {
        if (rule.getConditions() == null || rule.getConditions().isEmpty()) {
            return true; // No conditions = Public Access
        }
        for (Condition condition : rule.getConditions()) {
            if (!evaluateCondition(condition, request)) {
                return false; // One failed condition fails the whole rule
            }
        }
        return true;
    }

    private boolean evaluateCondition(Condition condition, AccessRequest request) {
        Object actualValue = request.getContext().get(condition.getAttribute());
        if (actualValue == null)
            return false;

        String expectedValue = condition.getValue();
        if (condition.getType() == ValueType.DYNAMIC) {
            Object dynamicValue = request.getContext().get(expectedValue);
            expectedValue = dynamicValue.toString();
        }

        return compareValues(actualValue.toString(), expectedValue, condition.getOperator());
    }

    private boolean compareValues(String actualValue, String expectedValue, Operator operator) {
        if (expectedValue == null)
            return false;

        switch (operator) {
            case EQUALS:
                return actualValue.equals(expectedValue);
            case NOT_EQUALS:
                return !actualValue.equals(expectedValue);
            case CONTAINS:
                return actualValue.contains(expectedValue);
            default:
                return false;
        }
    }

}
