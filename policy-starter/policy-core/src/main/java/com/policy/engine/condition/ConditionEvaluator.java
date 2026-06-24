package com.policy.engine.condition;

import com.policy.engine.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ConditionEvaluator {
    private static final Logger log = LoggerFactory.getLogger(ConditionEvaluator.class);

    public boolean evaluateConditions(List<Condition> conditions, AccessRequest request) {
        if (conditions == null || conditions.isEmpty()) return true;
        return evaluateConditionList(conditions, request, "AND");
    }

    private boolean evaluateConditionList(List<Condition> conditions, AccessRequest request, String defaultLogic) {
        if (conditions == null || conditions.isEmpty()) return true;

        Boolean result = null;
        String currentLogic = defaultLogic;

        for (Condition condition : conditions) {
            boolean matched;
            if (condition.isGroup()) {
                String groupLogic = condition.getLogicGroup();
                matched = evaluateConditionList(condition.getConditions(), request, groupLogic);
            } else {
                matched = evaluateSingleCondition(condition, request);
            }

            if (result == null) {
                result = matched;
            } else if ("AND".equalsIgnoreCase(currentLogic)) {
                result = result && matched;
            } else if ("OR".equalsIgnoreCase(currentLogic)) {
                result = result || matched;
            }
            currentLogic = condition.getLogicGroup() != null ? condition.getLogicGroup() : currentLogic;
        }
        return result != null && result;
    }

    private boolean evaluateSingleCondition(Condition condition, AccessRequest request) {
        Map<String, Object> context = request.getContext();
        Object actualValue = context.get(condition.getAttribute());

        if (actualValue == null && condition.getAttribute().equals("current_user")) {
            actualValue = request.getSubject();
        }

        if (actualValue == null) return false;

        String expectedValue = condition.getValue();
        if (condition.getType() == ValueType.DYNAMIC) {
            Object dynamicValue = context.get(expectedValue);
            if (dynamicValue == null) dynamicValue = request.getContext().get(expectedValue);
            if (dynamicValue == null) return false;
            expectedValue = dynamicValue.toString();
        }

        return compareValues(actualValue.toString(), expectedValue, condition.getOperator());
    }

    private boolean compareValues(String actualValue, String expectedValue, Operator operator) {
        if (expectedValue == null) return false;
        switch (operator) {
            case EQUALS: return actualValue.equals(expectedValue);
            case NOT_EQUALS: return !actualValue.equals(expectedValue);
            case CONTAINS: return actualValue.contains(expectedValue);
            case IN: return Arrays.asList(expectedValue.split("\\s*,\\s*")).contains(actualValue);
            case GREATER_THAN: return compareNumeric(actualValue, expectedValue) > 0;
            case LESS_THAN: return compareNumeric(actualValue, expectedValue) < 0;
            case MATCHES: return Pattern.compile(expectedValue).matcher(actualValue).find();
            case STARTS_WITH: return actualValue.startsWith(expectedValue);
            case ENDS_WITH: return actualValue.endsWith(expectedValue);
            default: return false;
        }
    }

    private int compareNumeric(String a, String b) {
        try {
            double da = Double.parseDouble(a);
            double db = Double.parseDouble(b);
            return Double.compare(da, db);
        } catch (NumberFormatException e) {
            return a.compareTo(b);
        }
    }
}
