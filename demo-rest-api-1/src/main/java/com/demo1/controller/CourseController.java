package com.demo1.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final Map<Long, Map<String, Object>> courses = new ConcurrentHashMap<>();

    public CourseController() {
        courses.put(1L, new HashMap<>(Map.of("id", 1L, "code", "CS101", "name", "Data Structures", "credits", 4)));
        courses.put(2L, new HashMap<>(Map.of("id", 2L, "code", "CS201", "name", "Algorithms", "credits", 4)));
        courses.put(3L, new HashMap<>(Map.of("id", 3L, "code", "EE101", "name", "Circuits", "credits", 3)));
    }

    @GetMapping
    public Collection<Map<String, Object>> getAll() { return courses.values(); }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        return courses.getOrDefault(id, Map.of("error", "not found"));
    }
}
