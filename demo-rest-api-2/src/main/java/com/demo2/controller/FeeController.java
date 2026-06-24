package com.demo2.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/fees")
public class FeeController {

    private final Map<String, Map<String, Object>> fees = new ConcurrentHashMap<>();

    public FeeController() {
        fees.put("alice", new HashMap<>(Map.of("studentId", "alice", "totalFee", 50000, "paid", 30000, "due", 20000)));
        fees.put("bob", new HashMap<>(Map.of("studentId", "bob", "totalFee", 45000, "paid", 45000, "due", 0)));
    }

    @GetMapping("/{studentId}")
    public Map<String, Object> getFee(@PathVariable String studentId) {
        return fees.getOrDefault(studentId, Map.of("error", "student not found"));
    }

    @GetMapping
    public Collection<Map<String, Object>> getAll() { return fees.values(); }
}
