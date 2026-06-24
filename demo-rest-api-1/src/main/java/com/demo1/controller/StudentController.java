package com.demo1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final Map<Long, Map<String, Object>> students = new ConcurrentHashMap<>();

    public StudentController() {
        students.put(1L, new HashMap<>(Map.of("id", 1L, "name", "Alice", "dept", "CSE", "year", 3)));
        students.put(2L, new HashMap<>(Map.of("id", 2L, "name", "Bob", "dept", "ECE", "year", 2)));
        students.put(3L, new HashMap<>(Map.of("id", 3L, "name", "Charlie", "dept", "ME", "year", 4)));
    }

    @GetMapping
    public Collection<Map<String, Object>> getAll() { return students.values(); }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long id) {
        Map<String, Object> s = students.get(id);
        return s != null ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        long id = students.keySet().stream().max(Long::compareTo).orElse(0L) + 1;
        Map<String, Object> s = new HashMap<>(body);
        s.put("id", id);
        students.put(id, s);
        return ResponseEntity.status(201).body(s);
    }
}
