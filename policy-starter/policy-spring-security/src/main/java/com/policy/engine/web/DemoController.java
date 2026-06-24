package com.policy.engine.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/public/info")
    public Map<String, String> publicInfo() {
        return Map.of(
            "application", "Policy-Driven Authorization Engine",
            "version", "1.0",
            "message", "This endpoint is publicly accessible"
        );
    }

    @GetMapping("/public/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/admin/users")
    public Map<String, Object> adminUsers() {
        return Map.of("users", new String[]{"Alice", "Bob", "Charlie"});
    }

    @GetMapping("/admin/settings")
    public Map<String, String> adminSettings() {
        return Map.of("theme", "dark", "language", "en", "timezone", "UTC");
    }

    @PostMapping("/admin/users")
    public Map<String, String> createUser(@RequestBody Map<String, Object> body) {
        return Map.of("status", "created", "name", body.getOrDefault("name", "unknown").toString());
    }

    @GetMapping("/profile/{id}")
    public Map<String, Object> getProfile(@PathVariable Long id, Authentication auth) {
        return Map.of(
            "id", id,
            "name", "User" + id,
            "requested_by", auth.getName()
        );
    }

    @PutMapping("/profile/{id}")
    public Map<String, String> updateProfile(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body,
                                              Authentication auth) {
        return Map.of(
            "status", "updated",
            "profile", String.valueOf(id),
            "by", auth.getName()
        );
    }
}
