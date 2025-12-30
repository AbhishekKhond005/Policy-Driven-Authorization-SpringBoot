package com.policy.engine.model;

public enum ValueType {
    LITERAL, // Treat value as a fixed string (e.g. "ADMIN")
    DYNAMIC // Treat value as a key to look up in context (e.g. "principal.username")
}