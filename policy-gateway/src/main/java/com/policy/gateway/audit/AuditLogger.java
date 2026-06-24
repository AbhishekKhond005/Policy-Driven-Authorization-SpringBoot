package com.policy.gateway.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class AuditLogger {
    private static final Logger log = LoggerFactory.getLogger(AuditLogger.class);
    private final Queue<AuditEvent> eventBuffer = new ConcurrentLinkedQueue<>();
    private final int maxBufferSize = 1000;

    public void log(AuditEvent event) {
        log.info("AUDIT: {} {} {} -> {} ({}ms) [{}]",
                event.getMethod(), event.getPath(), event.getStatus(),
                event.getPrincipal(), event.getDurationMs(), event.getCorrelationId());

        eventBuffer.offer(event);
        if (eventBuffer.size() > maxBufferSize) {
            eventBuffer.poll();
        }
    }

    public List<AuditEvent> getRecentEvents(int limit) {
        List<AuditEvent> events = new ArrayList<>(eventBuffer);
        int fromIndex = Math.max(0, events.size() - limit);
        return events.subList(fromIndex, events.size());
    }

    public List<AuditEvent> getDeniedEvents() {
        return eventBuffer.stream()
                .filter(e -> e.getStatus() == 403)
                .toList();
    }

    public void clear() { eventBuffer.clear(); }
}
