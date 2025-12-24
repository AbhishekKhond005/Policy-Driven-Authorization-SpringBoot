# Policy-Driven Authorization Engine

A Spring Boot application that externalizes REST API authorization using a declarative policy engine. Access decisions are driven by JSON-based policy rules — no hardcoded security logic.

---

## How It Works

```
HTTP Request
    │
    ▼
Spring Security FilterChain
    │
    ▼
PolicyAuthorizationManager  ──→  PolicyEngine  ──→  PolicyDefinition (JSON)
    │                                                    │
    ▼                                                    ▼
Grant / Deny                                      Rules + Conditions
```

Every request is intercepted by `PolicyAuthorizationManager`, which builds an `AccessRequest` (resource URI, HTTP method, user context) and delegates to `PolicyEngine`. The engine matches the request against loaded policy rules and returns `true`/`false`.

---

## Policy Format

Policies are defined as JSON arrays of rules:

```json
{
  "rules": [
    {
      "name": "Admin Access",
      "resource": "/api/admin/**",
      "actions": ["GET", "POST", "PUT", "DELETE"],
      "conditions": [
        { "attribute": "role", "operator": "EQUALS", "value": "ROLE_ADMIN" }
      ]
    },
    {
      "name": "Owner Access",
      "resource": "/api/profile/{id}",
      "actions": ["GET"],
      "conditions": [
        { "attribute": "resource_owner", "operator": "EQUALS", "value": "current_user", "type": "DYNAMIC" }
      ]
    },
    {
      "name": "Public",
      "resource": "/api/public/**",
      "actions": ["GET"],
      "conditions": []
    }
  ]
}
```

### Rule Fields

| Field        | Description                                                   |
|-------------|---------------------------------------------------------------|
| `name`       | Human-readable rule identifier                                |
| `resource`   | URI pattern (`/**` wildcard supported)                        |
| `actions`    | Allowed HTTP methods                                          |
| `conditions` | List of attribute-based conditions (AND logic)                |

### Condition Fields

| Field      | Description                                              |
|-----------|----------------------------------------------------------|
| `attribute` | Context key to check (e.g. `role`, `current_user`)       |
| `operator`  | `EQUALS`, `NOT_EQUALS`, `CONTAINS`                       |
| `value`     | Expected value or context key (see `type`)                |
| `type`      | `LITERAL` (default) or `DYNAMIC` (look up value in context) |

### Resource Matching

- `/api/admin/**` matches `/api/admin/users`, `/api/admin/settings`, etc.
- `/api/profile/1` matches exactly `/api/profile/1` (no wildcard segment)

---

## Integrating with Any REST API

### Step 1: Define Your Policy

Create `src/main/resources/policies/my-api-policy.json`:

```json
{
  "rules": [
    {
      "name": "User Read Own Data",
      "resource": "/api/users/{userId}/**",
      "actions": ["GET"],
      "conditions": [
        { "attribute": "current_user", "operator": "EQUALS", "value": "userId", "type": "DYNAMIC" }
      ]
    }
  ]
}
```

### Step 2: Configure in `application.yml`

```yaml
policy:
  file: classpath:policies/my-api-policy.json
```

### Step 3: Write Your REST Controller

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{userId}/profile")
    public Profile getProfile(@PathVariable Long userId) {
        // PolicyAuthorizationManager automatically checks
        // that current_user == userId before this runs
        return userService.findProfile(userId);
    }
}
```

### Step 4: Pass Context from Your Auth Mechanism

`PolicyAuthorizationManager` automatically extracts:
- `current_user` → from `Authentication.getName()`
- `role` → from `GrantedAuthority.getAuthority()`
- `remote_addr` → from `HttpServletRequest.getRemoteAddr()`

To add custom attributes, override the manager:

```java
@Component
public class CustomPolicyAuthorizationManager extends PolicyAuthorizationManager {

    public CustomPolicyAuthorizationManager(PolicyEngine policyEngine) {
        super(policyEngine);
    }

    @Override
    protected Map<String, Object> buildContext(HttpServletRequest request,
                                                Authentication authentication) {
        Map<String, Object> ctx = super.buildContext(request, authentication);
        ctx.put("department", request.getHeader("X-Department"));
        ctx.put("tenant_id", extractTenant(request));
        return ctx;
    }
}
```

---

## Running the Demo

```bash
# Start with admin policy (default)
mvn spring-boot:run

# Test admin endpoint
curl -u admin:admin http://localhost:8080/api/admin/users
# → 200 OK

# Test profile as admin (current_user = admin, not matching resource_owner)
curl -u admin:admin http://localhost:8080/api/profile/1
# → 403 Forbidden (admin is not the owner of profile/1)

# Test public endpoint (no auth)
curl http://localhost:8080/api/public/info
# → 200 OK
```

> Default credentials configured via Spring Boot's `spring.security.user.name` / `spring.security.user.password`.

---

## Switching Policies at Runtime

```java
@Autowired
private PolicyConfig policyConfig;

public void useUserPolicy() {
    PolicyDefinition userPolicy = new JsonPolicyLoader("classpath:policies/user-policy.json").load();
    policyEngine = new PolicyEngine(userPolicy);
}
```

Or use `FilePolicyLoader` to merge multiple files:

```java
PolicyLoader loader = new FilePolicyLoader(
    "classpath:policies/base-policy.json",
    "classpath:policies/tenant-override.json"
);
PolicyDefinition merged = loader.load();
PolicyEngine engine = new PolicyEngine(merged);
```

---

## Architecture

```
src/main/java/com/policy/engine/
├── PolicyApplication.java          # Spring Boot entry point
├── config/
│   ├── PolicyConfig.java           # Loads policy JSON → PolicyEngine bean
│   ├── PolicyAuthorizationManager.java  # Spring Security integration
│   └── SecurityConfig.java         # HTTP security rules
├── core/
│   └── PolicyEngine.java           # Rule evaluation engine
├── loader/
│   ├── PolicyLoader.java           # Interface
│   ├── JsonPolicyLoader.java       # Single-file JSON loader
│   └── FilePolicyLoader.java       # Multi-file merger
├── model/
│   ├── AccessRequest.java
│   ├── Condition.java
│   ├── Operator.java
│   ├── PolicyDefinition.java
│   ├── PolicyRule.java
│   └── ValueType.java
└── web/
    ├── DemoController.java         # Example endpoints
    └── GlobalExceptionHandler.java
```

---

## Commit History

```
2025-01-15  Migrate project to Spring Boot framework
2025-03-10  Integrate Spring Security with policy-driven authorization
2025-05-25  Add REST API demo layer and sample policy definitions
2025-08-01  Add classpath-aware policy loaders and multi-file support
2025-10-15  Add comprehensive integration guide and README
```

---

## Requirements

- Java 17+
- Maven 3.8+
- Spring Boot 3.2+
