package com.policy.engine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix = "policy")
public class PolicyEngineProperties {

    private String file = "classpath:policy.json";
    private List<String> files;
    private boolean enabled = true;
    private boolean defaultDeny = true;
    private Cache cache = new Cache();
    private Tenant tenant = new Tenant();

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }
    public List<String> getFiles() { return files; }
    public void setFiles(List<String> files) { this.files = files; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isDefaultDeny() { return defaultDeny; }
    public void setDefaultDeny(boolean defaultDeny) { this.defaultDeny = defaultDeny; }
    public Cache getCache() { return cache; }
    public void setCache(Cache cache) { this.cache = cache; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public static class Cache {
        private long ttlMs = 60_000;
        public long getTtlMs() { return ttlMs; }
        public void setTtlMs(long ttlMs) { this.ttlMs = ttlMs; }
    }

    public static class Tenant {
        private boolean enabled = false;
        private String headerName = "X-Tenant-Id";
        private String contextKey = "tenant";
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getHeaderName() { return headerName; }
        public void setHeaderName(String headerName) { this.headerName = headerName; }
        public String getContextKey() { return contextKey; }
        public void setContextKey(String contextKey) { this.contextKey = contextKey; }
    }
}
