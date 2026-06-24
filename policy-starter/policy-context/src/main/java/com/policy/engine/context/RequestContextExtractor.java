package com.policy.engine.context;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class RequestContextExtractor {

    public Map<String, Object> extract(HttpServletRequest request) {
        Map<String, Object> context = new HashMap<>();
        context.put("remote_addr", request.getRemoteAddr());
        context.put("method", request.getMethod());
        context.put("scheme", request.getScheme());
        context.put("host", request.getServerName());
        context.put("port", request.getServerPort());
        context.put("path", request.getRequestURI());
        context.put("query_string", request.getQueryString());
        context.put("user_agent", request.getHeader("User-Agent"));
        context.put("content_type", request.getContentType());
        context.put("accept", request.getHeader("Accept"));
        context.put("origin", request.getHeader("Origin"));
        context.put("referer", request.getHeader("Referer"));

        // Extract forwarded headers
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null) {
            context.put("forwarded_for", forwardedFor);
        }

        // Extract tenant from header
        String tenantId = request.getHeader("X-Tenant-Id");
        if (tenantId != null) {
            context.put("tenant", tenantId);
            context.put("tenant_id", tenantId);
        }

        return context;
    }
}
