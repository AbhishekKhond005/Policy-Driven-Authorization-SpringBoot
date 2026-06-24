package com.policy.gateway.config;

import com.policy.gateway.route.RouteMapping;
import com.policy.gateway.route.RouteRegistry;
import com.policy.gateway.route.ServiceTarget;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class RouteConfig {
    private static final Logger log = LoggerFactory.getLogger(RouteConfig.class);
    private final RouteRegistry routeRegistry;

    public RouteConfig(RouteRegistry routeRegistry) {
        this.routeRegistry = routeRegistry;
    }

    @PostConstruct
    public void initDefaultRoutes() {
        // Register default service targets
        ServiceTarget userService = new ServiceTarget();
        userService.setServiceId("user-service");
        userService.setBaseUrl("http://localhost:8081");
        routeRegistry.registerService(userService);

        ServiceTarget feeService = new ServiceTarget();
        feeService.setServiceId("fee-service");
        feeService.setBaseUrl("http://localhost:8082");
        routeRegistry.registerService(feeService);

        // Register default route mappings
        RouteMapping usersRoute = new RouteMapping();
        usersRoute.setId("users-api");
        usersRoute.setIncomingPath("/api/users/**");
        usersRoute.setTargetUrl("http://localhost:8081");
        usersRoute.setMethods(Set.of("GET", "POST", "PUT", "DELETE"));
        usersRoute.setStripPrefix(true);
        routeRegistry.registerRoute(usersRoute);

        RouteMapping feesRoute = new RouteMapping();
        feesRoute.setId("fees-api");
        feesRoute.setIncomingPath("/api/fees/**");
        feesRoute.setTargetUrl("http://localhost:8082");
        feesRoute.setMethods(Set.of("GET", "POST"));
        feesRoute.setStripPrefix(true);
        routeRegistry.registerRoute(feesRoute);

        log.info("Default routes and services initialized");
    }
}
