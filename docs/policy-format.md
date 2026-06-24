# Policy Format

Policies are JSON files defining access control rules.

## Structure

```json
{
  "version": "2.0",
  "id": "main-policy",
  "description": "Main access control policy",
  "defaultDeny": true,
  "rules": [
    {
      "name": "Rule name",
      "resource": "/api/**",
      "actions": ["GET", "POST"],
      "effect": "ALLOW",
      "priority": 100,
      "audit": true,
      "roles": ["ADMIN"],
      "tenants": ["CSE"],
      "scopes": ["read"],
      "conditions": [
        {
          "attribute": "role",
          "operator": "EQUALS",
          "value": "ROLE_ADMIN",
          "type": "LITERAL"
        }
      ]
    }
  ]
}
```

## Fields

### PolicyDefinition
| Field | Type | Description |
|-------|------|-------------|
| `version` | string | Policy schema version |
| `id` | string | Unique policy identifier |
| `description` | string | Human-readable description |
| `defaultDeny` | boolean | Deny if no rule matches (default: true) |
| `rules` | array | List of policy rules |

### PolicyRule
| Field | Type | Description |
|-------|------|-------------|
| `name` | string | Rule name (logged in decisions) |
| `resource` | string | Ant-style path pattern (`/api/**`, `/api/users/{id}`) |
| `actions` | array | HTTP methods (`GET`, `POST`, `PUT`, `DELETE`) |
| `effect` | enum | `ALLOW` or `DENY` |
| `priority` | int | Lower = higher priority (DENY checked first) |
| `audit` | boolean | Log decision when this rule matches |
| `roles` | array | Required roles (e.g., `["ADMIN"]`) |
| `tenants` | array | Required tenant IDs |
| `scopes` | array | Required OAuth scopes |
| `conditions` | array | Condition expressions |

### Condition
| Field | Type | Description |
|-------|------|-------------|
| `attribute` | string | Context attribute to check |
| `operator` | enum | See operators below |
| `value` | string | Expected value (or context key for DYNAMIC) |
| `type` | enum | `LITERAL` or `DYNAMIC` |
| `logicGroup` | string | `AND` or `OR` for nested conditions |
| `conditions` | array | Nested sub-conditions |

## Operators

| Operator | Description |
|----------|-------------|
| `EQUALS` | Exact string match |
| `NOT_EQUALS` | Negated string match |
| `CONTAINS` | Substring match |
| `IN` | Comma-separated list membership |
| `GREATER_THAN` | Numeric comparison |
| `LESS_THAN` | Numeric comparison |
| `MATCHES` | Regex pattern match |
| `STARTS_WITH` | Prefix match |
| `ENDS_WITH` | Suffix match |

## AND/OR Condition Groups

```json
{
  "conditions": [
    {
      "logicGroup": "OR",
      "conditions": [
        { "attribute": "role", "operator": "EQUALS", "value": "ROLE_ADMIN" },
        { "attribute": "role", "operator": "EQUALS", "value": "ROLE_MANAGER" }
      ]
    }
  ]
}
```
