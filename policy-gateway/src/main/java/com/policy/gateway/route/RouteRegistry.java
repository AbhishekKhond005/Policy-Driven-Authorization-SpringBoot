package com.policy.gateway.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RouteRegistry {
    private static final Logger log = LoggerFactory.getLogger(RouteRegistry.class);
    private final Map<String, RouteMapping> routes = new ConcurrentHashMap<>();
    private final Map<String, ServiceTarget> services = new ConcurrentHashMap<>();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public void registerRoute(RouteMapping route) {
        routes.put(route.getId(), route);
        log.info("Route registered: {} -> {} [{}]", route.getIncomingPath(), route.getTargetUrl(), route.getMethods());
    }

    public void unregisterRoute(String id) {
        routes.remove(id);
    }

    public RouteMapping matchRoute(String path, String method) {
        for (RouteMapping route : routes.values()) {
            if (!route.isEnabled()) continue;
            if (!pathMatcher.match(route.getIncomingPath(), path)) continue;
            if (route.getMethods() != null && !route.getMethods().isEmpty()
                    && !route.getMethods().contains(method.toUpperCase())) continue;
            return route;
        }
        return null;
    }

    public void registerService(ServiceTarget service) {
        services.put(service.getServiceId(), service);
    }

    public ServiceTarget getService(String serviceId) {
        return services.get(serviceId);
    }

    public List<RouteMapping> getAllRoutes() { return new ArrayList<>(routes.values()); }
    public List<ServiceTarget> getAllServices() { return new ArrayList<>(services.values()); }

    public Map<String, String> extractPathVariables(String pattern, String path) {
        return pathMatcher.extractUriTemplateVariables(pattern, path);
    }
}
