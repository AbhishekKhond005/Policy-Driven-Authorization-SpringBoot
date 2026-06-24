package com.policy.engine.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.policy.engine.model.PolicyDefinition;
import com.policy.engine.model.PolicyRule;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FilePolicyLoader implements PolicyLoader {

    private final List<String> locations;
    private final ObjectMapper objectMapper;

    public FilePolicyLoader(String... locations) {
        this.locations = Arrays.asList(locations);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public PolicyDefinition load() {
        List<PolicyRule> allRules = new ArrayList<>();

        for (String location : locations) {
            try (InputStream input = openStream(location)) {
                PolicyDefinition def = objectMapper.readValue(input, PolicyDefinition.class);
                if (def.getRules() != null) {
                    allRules.addAll(def.getRules());
                }
            } catch (IOException e) {
                System.err.println("Warning: Could not load policy file: " + location + " - " + e.getMessage());
            }
        }

        PolicyDefinition combined = new PolicyDefinition();
        combined.setRules(allRules);
        return combined;
    }

    private InputStream openStream(String location) throws IOException {
        if (location.startsWith("classpath:")) {
            String path = location.substring("classpath:".length());
            InputStream is = getClass().getClassLoader().getResourceAsStream(path);
            if (is == null) {
                throw new FileNotFoundException("Policy file not found on classpath: " + path);
            }
            return is;
        }
        return Files.newInputStream(Paths.get(location));
    }
}
