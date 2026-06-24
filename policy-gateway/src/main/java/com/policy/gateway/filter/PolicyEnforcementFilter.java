package com.policy.gateway.filter;

import com.policy.engine.core.PolicyEngine;
import com.policy.engine.model.AccessRequest;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(2)
public class PolicyEnforcementFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(PolicyEnforcementFilter.class);
    private final PolicyEngine policyEngine;

    public PolicyEnforcementFilter(PolicyEngine policyEngine) {
        this.policyEngine = policyEngine;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();
        if (path.startsWith("/actuator") || path.startsWith("/api/public")) {
            chain.doFilter(request, response);
            return;
        }

        Map<String, Object> context = new HashMap<>();
        context.put("current_user", request.getAttribute("principal"));
        context.put("role", request.getAttribute("roles"));
        context.put("remote_addr", request.getRemoteAddr());
        context.put("method", request.getMethod());

        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("claims");
        if (claims != null) {
            context.putAll(claims);
        }

        AccessRequest accessRequest = new AccessRequest(
                request.getAttribute("principal") != null ? request.getAttribute("principal").toString() : "anonymous",
                path, request.getMethod(), context);

        if (!policyEngine.evaluate(accessRequest)) {
            response.setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"FORBIDDEN\",\"message\":\"Access denied by policy\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
