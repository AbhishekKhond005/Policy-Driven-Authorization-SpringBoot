package com.policy.admin.service;

import com.policy.admin.model.RouteDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RouteService {
    private static final Logger log = LoggerFactory.getLogger(RouteService.class);
    private final Map<String, RouteDocument> routes = new ConcurrentHashMap<>();

    public RouteDocument createRoute(RouteDocument document) {
        String id = UUID.randomUUID().toString();
        document.setId(id);
        routes.put(id, document);
        log.info("Route created: {} -> {} [{}]", document.getIncomingPath(), document.getTargetUrl(), document.getMethods());
        return document;
    }

    public RouteDocument getRoute(String id) { return routes.get(id); }

    public List<RouteDocument> getAllRoutes() { return new ArrayList<>(routes.values()); }

    public RouteDocument updateRoute(String id, RouteDocument document) {
        if (!routes.containsKey(id)) return null;
        document.setId(id);
        routes.put(id, document);
        return document;
    }

    public boolean deleteRoute(String id) { return routes.remove(id) != null; }

    public boolean toggleRoute(String id) {
        RouteDocument route = routes.get(id);
        if (route == null) return false;
        route.setEnabled(!route.isEnabled());
        return true;
    }
}
