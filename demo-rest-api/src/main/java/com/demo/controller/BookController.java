package com.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class BookController {

    private final Map<Long, Map<String, Object>> books = new ConcurrentHashMap<>();

    public BookController() {
        books.put(1L, new HashMap<>(Map.of("id", 1L, "title", "The Great Gatsby", "author", "F. Scott Fitzgerald")));
        books.put(2L, new HashMap<>(Map.of("id", 2L, "title", "1984", "author", "George Orwell")));
        books.put(3L, new HashMap<>(Map.of("id", 3L, "title", "To Kill a Mockingbird", "author", "Harper Lee")));
    }

    @GetMapping("/public/books")
    public Collection<Map<String, Object>> listPublicBooks() {
        return books.values();
    }

    @GetMapping("/admin/books")
    public Collection<Map<String, Object>> listAllBooks() {
        return books.values();
    }

    @PostMapping("/admin/books")
    public ResponseEntity<Map<String, Object>> createBook(@RequestBody Map<String, Object> body) {
        long id = books.keySet().stream().max(Long::compareTo).orElse(0L) + 1;
        Map<String, Object> book = new HashMap<>(body);
        book.put("id", id);
        books.put(id, book);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @PutMapping("/admin/books/{id}")
    public ResponseEntity<Map<String, Object>> updateBook(@PathVariable Long id,
                                                           @RequestBody Map<String, Object> body) {
        Map<String, Object> existing = books.get(id);
        if (existing == null) return ResponseEntity.notFound().build();
        existing.putAll(body);
        existing.put("id", id);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/admin/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (books.remove(id) != null) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Map<String, Object>> getBook(@PathVariable Long id) {
        Map<String, Object> book = books.get(id);
        if (book == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(book);
    }
}
