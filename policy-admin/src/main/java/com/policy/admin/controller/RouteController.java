package com.policy.admin.controller;

import com.policy.admin.model.RouteDocument;
import com.policy.admin.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/routes")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) { this.routeService = routeService; }

    @PostMapping
    public ResponseEntity<RouteDocument> create(@RequestBody RouteDocument document) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.createRoute(document));
    }

    @GetMapping
    public List<RouteDocument> list() { return routeService.getAllRoutes(); }

    @GetMapping("/{id}")
    public ResponseEntity<RouteDocument> get(@PathVariable String id) {
        RouteDocument doc = routeService.getRoute(id);
        return doc != null ? ResponseEntity.ok(doc) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteDocument> update(@PathVariable String id, @RequestBody RouteDocument document) {
        RouteDocument updated = routeService.updateRoute(id, document);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return routeService.deleteRoute(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<Void> toggle(@PathVariable String id) {
        return routeService.toggleRoute(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
