package com.policy.engine.model;

import java.util.List;
import java.util.Set;

public class PolicyRule {
    private String name;
    private String resource;
    private Set<String> actions;
    private List<Condition> conditions;
    private int priority = 1000;
    private Effect effect = Effect.ALLOW;
    private boolean audit = true;
    private Set<String> roles;
    private Set<String> tenants;
    private Set<String> scopes;
    private long ttlSeconds = 0;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public Set<String> getActions() { return actions; }
    public void setActions(Set<String> actions) { this.actions = actions; }
    public List<Condition> getConditions() { return conditions; }
    public void setConditions(List<Condition> conditions) { this.conditions = conditions; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public Effect getEffect() { return effect; }
    public void setEffect(Effect effect) { this.effect = effect; }
    public boolean isAudit() { return audit; }
    public void setAudit(boolean audit) { this.audit = audit; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    public Set<String> getTenants() { return tenants; }
    public void setTenants(Set<String> tenants) { this.tenants = tenants; }
    public Set<String> getScopes() { return scopes; }
    public void setScopes(Set<String> scopes) { this.scopes = scopes; }
    public long getTtlSeconds() { return ttlSeconds; }
    public void setTtlSeconds(long ttlSeconds) { this.ttlSeconds = ttlSeconds; }
}
