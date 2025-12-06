package com.policy.engine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.policy.engine.core.PolicyEngine;
import com.policy.engine.model.PolicyDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

@Configuration
public class PolicyConfig {

    @Bean
    public PolicyEngine policyEngine(@Value("${policy.file}") String policyFile,
                                     ResourceLoader resourceLoader) throws IOException {
        Resource resource = resourceLoader.getResource(policyFile);
        ObjectMapper mapper = new ObjectMapper();
        PolicyDefinition definition = mapper.readValue(resource.getInputStream(), PolicyDefinition.class);
        return new PolicyEngine(definition);
    }
}
