package com.bank.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final Map<String, Map<String, Object>> accounts = new ConcurrentHashMap<>();

    public AccountController() {
        accounts.put("ACC-001", Map.of("id", "ACC-001", "owner", "alice", "balance", 5000.0));
        accounts.put("ACC-002", Map.of("id", "ACC-002", "owner", "bob", "balance", 3000.0));
    }

    @GetMapping
    public Map<String, Object> listAccounts() {
        return Map.of("accounts", accounts.values());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getAccount(@PathVariable String id) {
        Map<String, Object> account = accounts.get(id);
        if (account == null) return Map.of("error", "not found");
        return account;
    }
}
