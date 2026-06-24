package com.policy.engine.model;

import java.util.List;

public class PolicyDefinition {
    private String version;
    private String id;
    private String description;
    private boolean defaultDeny = true;
    private List<PolicyRule> rules;

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isDefaultDeny() { return defaultDeny; }
    public void setDefaultDeny(boolean defaultDeny) { this.defaultDeny = defaultDeny; }
    public List<PolicyRule> getRules() { return rules; }
    public void setRules(List<PolicyRule> rules) { this.rules = rules; }
}
