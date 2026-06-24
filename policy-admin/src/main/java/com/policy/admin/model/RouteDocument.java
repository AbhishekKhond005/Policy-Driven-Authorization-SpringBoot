package com.policy.admin.model;

import java.util.Map;
import java.util.Set;

public class RouteDocument {
    private String id;
    private String incomingPath;
    private String targetUrl;
    private Set<String> methods;
    private String policyRef;
    private boolean enabled = true;
    private boolean stripPrefix = true;
    private Set<String> requiredRoles;
    private Set<String> requiredTenants;
    private Map<String, String> metadata;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIncomingPath() { return incomingPath; }
    public void setIncomingPath(String incomingPath) { this.incomingPath = incomingPath; }
    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
    public Set<String> getMethods() { return methods; }
    public void setMethods(Set<String> methods) { this.methods = methods; }
    public String getPolicyRef() { return policyRef; }
    public void setPolicyRef(String policyRef) { this.policyRef = policyRef; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isStripPrefix() { return stripPrefix; }
    public void setStripPrefix(boolean stripPrefix) { this.stripPrefix = stripPrefix; }
    public Set<String> getRequiredRoles() { return requiredRoles; }
    public void setRequiredRoles(Set<String> requiredRoles) { this.requiredRoles = requiredRoles; }
    public Set<String> getRequiredTenants() { return requiredTenants; }
    public void setRequiredTenants(Set<String> requiredTenants) { this.requiredTenants = requiredTenants; }
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
}
