package com.policy.engine.model;

import java.util.List;

public class Condition {
    private String attribute;
    private Operator operator;
    private String value;
    private ValueType type = ValueType.LITERAL;
    private String logicGroup;
    private List<Condition> conditions;

    public Condition() {}

    public Condition(String attribute, Operator operator, String value, ValueType type) {
        this.attribute = attribute;
        this.operator = operator;
        this.value = value;
        this.type = type;
    }

    public String getAttribute() { return attribute; }
    public void setAttribute(String attribute) { this.attribute = attribute; }
    public Operator getOperator() { return operator; }
    public void setOperator(Operator operator) { this.operator = operator; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public ValueType getType() { return type; }
    public void setType(ValueType type) { this.type = type; }
    public String getLogicGroup() { return logicGroup; }
    public void setLogicGroup(String logicGroup) { this.logicGroup = logicGroup; }
    public List<Condition> getConditions() { return conditions; }
    public void setConditions(List<Condition> conditions) { this.conditions = conditions; }

    public boolean isGroup() { return logicGroup != null && conditions != null && !conditions.isEmpty(); }
}
