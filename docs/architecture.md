# Architecture: Zero-Trust API Access Control Platform

## Overview

The platform enforces policy-based, zero-trust authorization before forwarding traffic to backend services. Clients never talk directly to protected services — they go through the control layer.

```
Client
  |
  v
Zero-Trust Gateway / Access Control Layer
  |-- Authentication filter
  |-- Policy engine
  |-- Tenant resolver
  |-- Route registry
  |-- Audit logger
  |-- Rate limiter
  |-- Request forwarder
  |
  v
Protected backend services
  |-- user-service
  |-- fee-service
  |-- admin-service
  |-- (any service)
```

## Module Structure

```
policy-platform/
|-- policy-starter/          (Reusable starter — publishable as Maven artifact)
|   |-- policy-core/         (Policy model, engine, condition evaluator, loaders)
|   |-- policy-spring-security/  (Spring Security integration, auto-configuration)
|   |-- policy-context/      (Request context extraction, JWT claim resolution)
|-- policy-gateway/          (Runtime enforcement — sits in front of services)
|-- policy-admin/            (Management API — CRUD for policies, routes, simulation)
|-- policy-samples/          (Example services)
|   |-- banking-service/
|   |-- (university-service in demo-rest-api/)
|-- demo-rest-api/           (Existing demo app demonstrating starter usage)
```

## Key Design Decisions

### 1. Modularity
- The starter module is publishable as a Maven dependency
- Runtime and admin modules build on the starter
- Samples demonstrate reuse

### 2. Policy Evaluation
- DENY rules evaluated first (take precedence over ALLOW)
- Rules sorted by priority (lower number = higher priority)
- AND/OR condition groups supported
- Default deny when no rule matches

### 3. Gateway Pattern
- All requests pass through authentication, policy enforcement, and audit filters
- Matched routes are forwarded to target services via HTTP
- Unauthorized requests blocked at the gateway

### 4. Multi-Tenancy
- Tenant extracted from `X-Tenant-Id` header
- Tenant-aware policy rules and route restrictions
