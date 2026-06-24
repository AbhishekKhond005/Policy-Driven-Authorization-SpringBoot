package com.policy.engine.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.policy.engine.model.PolicyDefinition;

import java.io.*;
import java.nio.file.*;

public class JsonPolicyLoader implements PolicyLoader {

    private final String location;
    private final ObjectMapper objectMapper;

    public JsonPolicyLoader(String location) {
        this.location = location;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public PolicyDefinition load() {
        try (InputStream input = resolveInputStream()) {
            return objectMapper.readValue(input, PolicyDefinition.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load policy definition from: " + location, e);
        }
    }

    private InputStream resolveInputStream() throws IOException {
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
