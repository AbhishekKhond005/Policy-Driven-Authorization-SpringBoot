package com.policy.gateway.filter;

import com.policy.engine.context.JwtClaimResolver;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Order(1)
public class AuthenticationFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final JwtClaimResolver claimResolver;

    public AuthenticationFilter() {
        this.claimResolver = new JwtClaimResolver();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Map<String, Object> claims = claimResolver.resolveClaims(token);
            request.setAttribute("claims", claims);
            request.setAttribute("principal", claims.getOrDefault("sub", "anonymous"));
            request.setAttribute("roles", claims.getOrDefault("roles", claims.getOrDefault("role", "")));
        }

        chain.doFilter(request, response);
    }
}
