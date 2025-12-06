package com.policy.engine.config;

import com.policy.engine.core.PolicyEngine;
import com.policy.engine.model.AccessRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
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
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            requestContext.put("role", authority.getAuthority());
        }
        requestContext.put("remote_addr", request.getRemoteAddr());
        requestContext.put("user_agent", request.getHeader("User-Agent"));

        AccessRequest accessRequest = new AccessRequest(
                authentication.getName(), resource, action, requestContext);

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
