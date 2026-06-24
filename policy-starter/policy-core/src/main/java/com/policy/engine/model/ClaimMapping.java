package com.policy.engine.model;

public class ClaimMapping {
    private String claimName;
    private String contextKey;
    private String defaultValue;
    private boolean required = false;

    public String getClaimName() { return claimName; }
    public void setClaimName(String claimName) { this.claimName = claimName; }
    public String getContextKey() { return contextKey; }
    public void setContextKey(String contextKey) { this.contextKey = contextKey; }
    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}
