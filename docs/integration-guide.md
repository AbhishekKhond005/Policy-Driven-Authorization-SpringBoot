# Integration Guide

## Adding the Starter to Another Project

### 1. Add Dependency

```xml
<dependency>
    <groupId>com.policy</groupId>
    <artifactId>policy-spring-security</artifactId>
    <version>2.0-SNAPSHOT</version>
</dependency>
```

### 2. Configure Policy Source

```yaml
policy:
  file: classpath:policies/my-policy.json
  default-deny: true
  cache:
    ttl-ms: 60000
```

### 3. Define Policy File

Create `src/main/resources/policies/my-policy.json`:

```json
{
  "rules": [
    {
      "name": "Public Access",
      "resource": "/api/public/**",
      "actions": ["GET"],
      "effect": "ALLOW",
      "priority": 1000
    },
    {
      "name": "Admin Access",
      "resource": "/api/admin/**",
      "actions": ["GET", "POST", "PUT", "DELETE"],
      "effect": "ALLOW",
      "priority": 100,
      "conditions": [
        { "attribute": "role", "operator": "EQUALS", "value": "ROLE_ADMIN" }
      ]
    }
  ]
}
```

### 4. Secure Endpoints

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           PolicyAuthorizationManager authz) {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().access(authz)
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
```

### 5. Running the Gateway

```bash
# Build the platform
mvn clean install -DskipTests

# Run the gateway
mvn spring-boot:run -pl policy-gateway

# Run the admin API
mvn spring-boot:run -pl policy-admin
```

## Extension Points

- Implement custom `PolicyLoader` for database-backed policies
- Add custom context extractors by extending `RequestContextExtractor`
- Implement custom condition operators in `ConditionEvaluator`
