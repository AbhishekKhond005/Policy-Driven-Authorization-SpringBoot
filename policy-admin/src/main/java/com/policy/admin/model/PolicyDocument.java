package com.policy.admin.model;

import com.policy.engine.model.PolicyDefinition;

public class PolicyDocument {
    private String id;
    private String name;
    private String version;
    private boolean enabled = true;
    private PolicyDefinition policyDefinition;
    private long createdAt;
    private long updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public PolicyDefinition getPolicyDefinition() { return policyDefinition; }
    public void setPolicyDefinition(PolicyDefinition policyDefinition) { this.policyDefinition = policyDefinition; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
