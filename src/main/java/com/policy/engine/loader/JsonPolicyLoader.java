package com.policy.engine.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.policy.engine.model.PolicyDefinition;

import java.io.File;
import java.io.IOException;

public class JsonPolicyLoader implements PolicyLoader {

    private final String filePath;
    private final ObjectMapper objectMapper;

    public JsonPolicyLoader(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public PolicyDefinition load() {
        try {
            return objectMapper.readValue(new File(filePath), PolicyDefinition.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load policy definition from: " + filePath, e);
        }
    }
}