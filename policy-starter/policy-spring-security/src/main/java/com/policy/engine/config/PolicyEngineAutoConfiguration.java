package com.policy.engine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.policy.engine.core.PolicyEngine;
import com.policy.engine.loader.FilePolicyLoader;
import com.policy.engine.loader.JsonPolicyLoader;
import com.policy.engine.loader.PolicyLoader;
import com.policy.engine.model.PolicyDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties(PolicyEngineProperties.class)
@ConditionalOnProperty(name = "policy.enabled", matchIfMissing = true)
public class PolicyEngineAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PolicyEngine policyEngine(PolicyEngineProperties properties,
                                     ResourceLoader resourceLoader) throws IOException {
        PolicyDefinition definition;
        
        if (properties.getFiles() != null && !properties.getFiles().isEmpty()) {
            // Load from multiple files
            FilePolicyLoader loader = new FilePolicyLoader(
                properties.getFiles().toArray(new String[0])
            );
            definition = loader.load();
        } else {
            // Load from single file
            Resource resource = resourceLoader.getResource(properties.getFile());
            ObjectMapper mapper = new ObjectMapper();
            definition = mapper.readValue(resource.getInputStream(), PolicyDefinition.class);
        }
        
        definition.setDefaultDeny(properties.isDefaultDeny());
        return new PolicyEngine(definition, properties.getCache().getTtlMs());
    }

    @Bean
    @ConditionalOnMissingBean
    public PolicyAuthorizationManager policyAuthorizationManager(PolicyEngine policyEngine) {
        return new PolicyAuthorizationManager(policyEngine);
    }
}
