package com.policy.gateway.filter;

import com.policy.gateway.audit.AuditEvent;
import com.policy.gateway.audit.AuditLogger;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@Order(3)
public class AuditLoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(AuditLoggingFilter.class);
    private final AuditLogger auditLogger;

    public AuditLoggingFilter(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String correlationId = UUID.randomUUID().toString();
        request.setAttribute("correlationId", correlationId);

        long startTime = System.currentTimeMillis();

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            AuditEvent event = new AuditEvent();
            event.setCorrelationId(correlationId);
            event.setTimestamp(Instant.now());
            event.setMethod(request.getMethod());
            event.setPath(request.getRequestURI());
            event.setStatus(status);
            event.setDurationMs(duration);
            event.setPrincipal(request.getAttribute("principal") != null
                    ? request.getAttribute("principal").toString() : "anonymous");
            event.setRemoteAddr(request.getRemoteAddr());

            auditLogger.log(event);

            if (status == 403) {
                log.warn("AUDIT: DENIED {} {} -> {} ({}ms)", request.getMethod(),
                        request.getRequestURI(), status, duration);
            }
        }
    }
}
