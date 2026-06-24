package com.policy.gateway.rate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiter {
    private static final Logger log = LoggerFactory.getLogger(RateLimiter.class);
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStart = new ConcurrentHashMap<>();
    private final int maxRequests;
    private final long windowMs;

    public RateLimiter() {
        this.maxRequests = 100;
        this.windowMs = 60_000;
    }

    public RateLimiter(int maxRequests, long windowMs) {
        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
    }

    public boolean isAllowed(String clientKey) {
        long now = System.currentTimeMillis();
        windowStart.putIfAbsent(clientKey, now);
        requestCounts.putIfAbsent(clientKey, new AtomicInteger(0));

        long window = windowStart.get(clientKey);
        if (now - window > windowMs) {
            windowStart.put(clientKey, now);
            requestCounts.put(clientKey, new AtomicInteger(1));
            return true;
        }

        int count = requestCounts.get(clientKey).incrementAndGet();
        if (count > maxRequests) {
            log.warn("RATE_LIMIT: client={} count={}/{}", clientKey, count, maxRequests);
            return false;
        }
        return true;
    }

    public void reset(String clientKey) {
        requestCounts.remove(clientKey);
        windowStart.remove(clientKey);
    }

    public int getRemainingRequests(String clientKey) {
        AtomicInteger count = requestCounts.get(clientKey);
        if (count == null) return maxRequests;
        return Math.max(0, maxRequests - count.get());
    }
}
