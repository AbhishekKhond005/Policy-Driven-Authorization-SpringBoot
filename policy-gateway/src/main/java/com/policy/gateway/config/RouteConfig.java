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
        ServiceTarget collegeService = new ServiceTarget();
        collegeService.setServiceId("college-service");
        collegeService.setBaseUrl("http://localhost:8081");
        routeRegistry.registerService(collegeService);

        ServiceTarget marksFeeService = new ServiceTarget();
        marksFeeService.setServiceId("marks-fee-service");
        marksFeeService.setBaseUrl("http://localhost:8082");
        routeRegistry.registerService(marksFeeService);

        // Register default route mappings
        RouteMapping studentsRoute = new RouteMapping();
        studentsRoute.setId("students-api");
        studentsRoute.setIncomingPath("/api/students/**");
        studentsRoute.setTargetUrl("http://localhost:8081");
        studentsRoute.setMethods(Set.of("GET", "POST"));
        studentsRoute.setStripPrefix(true);
        routeRegistry.registerRoute(studentsRoute);

        RouteMapping coursesRoute = new RouteMapping();
        coursesRoute.setId("courses-api");
        coursesRoute.setIncomingPath("/api/courses/**");
        coursesRoute.setTargetUrl("http://localhost:8081");
        coursesRoute.setMethods(Set.of("GET"));
        coursesRoute.setStripPrefix(true);
        routeRegistry.registerRoute(coursesRoute);

        RouteMapping feesRoute = new RouteMapping();
        feesRoute.setId("fees-api");
        feesRoute.setIncomingPath("/api/fees/**");
        feesRoute.setTargetUrl("http://localhost:8082");
        feesRoute.setMethods(Set.of("GET"));
        feesRoute.setStripPrefix(true);
        routeRegistry.registerRoute(feesRoute);

        RouteMapping marksRoute = new RouteMapping();
        marksRoute.setId("marks-api");
        marksRoute.setIncomingPath("/api/marks/**");
        marksRoute.setTargetUrl("http://localhost:8082");
        marksRoute.setMethods(Set.of("GET"));
        marksRoute.setStripPrefix(true);
        routeRegistry.registerRoute(marksRoute);

        log.info("Default routes and services initialized");
    }
}
