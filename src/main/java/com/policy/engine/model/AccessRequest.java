package com.policy.engine.model;

import java.util.Map;

public class AccessRequest {
    private String subject;
    private String resource;
    private String action;
    private Map<String, Object> context;

    public AccessRequest(String subject, String resource, String action, Map<String, Object> context) {
        this.subject = subject;
        this.resource = resource;
        this.action = action;
        this.context = context;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }



    
}
