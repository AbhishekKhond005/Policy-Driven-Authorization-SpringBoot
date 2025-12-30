package com.policy.engine.model;

import java.util.List;
import java.util.Set;

public class PolicyRule {
    private String name;
    private String resource;
    private Set<String> actions;
    private List<Condition> conditions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    // store in a list variable and return to avoid PolicyRule.getActions().clear();
    public Set<String> getActions() {
        return actions;
    }

    public void setActions(Set<String> actions) {
        this.actions = actions;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
