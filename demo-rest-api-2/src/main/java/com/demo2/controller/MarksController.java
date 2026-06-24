package com.demo2.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/marks")
public class MarksController {

    private final Map<String, List<Map<String, Object>>> marks = new ConcurrentHashMap<>();

    public MarksController() {
        marks.put("alice", List.of(
            Map.of("course", "CS101", "marks", 85, "grade", "A"),
            Map.of("course", "CS201", "marks", 72, "grade", "B+")
        ));
        marks.put("bob", List.of(
            Map.of("course", "EE101", "marks", 90, "grade", "A"),
            Map.of("course", "CS101", "marks", 78, "grade", "B")
        ));
    }

    @GetMapping("/{studentId}")
    public Map<String, Object> getMarks(@PathVariable String studentId) {
        List<Map<String, Object>> result = marks.get(studentId);
        if (result == null) return Map.of("error", "student not found");
        return Map.of("studentId", studentId, "marks", result);
    }

    @GetMapping
    public Map<String, Object> getAll() {
        return Map.of("marks", marks);
    }
}
