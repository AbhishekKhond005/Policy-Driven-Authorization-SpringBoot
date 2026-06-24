package com.policy.engine.config;

import com.policy.engine.core.PolicyEngine;
import com.policy.engine.model.AccessRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PolicyAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final PolicyEngine policyEngine;

    public PolicyAuthorizationManager(PolicyEngine policyEngine) {
        this.policyEngine = policyEngine;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
                                       RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        String resource = request.getRequestURI();
        String action = request.getMethod();

        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            authentication = createAnonymousAuth();
        }

        Map<String, Object> requestContext = new HashMap<>();
        requestContext.put("current_user", authentication.getName());
        requestContext.put("subject", authentication.getName());
        
        // Extract roles/authorities
        StringBuilder roles = new StringBuilder();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (roles.length() > 0) roles.append(",");
            roles.append(role);
            if (role.startsWith("ROLE_")) {
                requestContext.put("role", role);
            }
        }
        requestContext.put("roles", roles.toString());
        
        // Extract additional context from request
        requestContext.put("remote_addr", request.getRemoteAddr());
        requestContext.put("user_agent", request.getHeader("User-Agent"));
        requestContext.put("method", request.getMethod());
        
        // Extract tenant from header or path
        String tenantId = request.getHeader("X-Tenant-Id");
        if (tenantId != null) {
            requestContext.put("tenant", tenantId);
        }

        // Extract JWT claims if available (via header)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            requestContext.put("token_type", "bearer");
        }

        AccessRequest accessRequest = new AccessRequest(
                authentication.getName(), resource, action, requestContext);
        accessRequest.setRemoteAddr(request.getRemoteAddr());
        accessRequest.setUserAgent(request.getHeader("User-Agent"));
        accessRequest.setTenantId(tenantId);

        boolean granted = policyEngine.evaluate(accessRequest);
        return new AuthorizationDecision(granted);
    }

    private Authentication createAnonymousAuth() {
        return new Authentication() {
            @Override
            public String getName() { return "anonymous"; }
            @Override
            public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
                return java.util.List.of(() -> "ROLE_ANONYMOUS");
            }
            @Override
            public Object getCredentials() { return null; }
            @Override
            public Object getDetails() { return null; }
            @Override
            public Object getPrincipal() { return "anonymous"; }
            @Override
            public boolean isAuthenticated() { return false; }
            @Override
            public void setAuthenticated(boolean isAuthenticated) {}
        };
    }
}
