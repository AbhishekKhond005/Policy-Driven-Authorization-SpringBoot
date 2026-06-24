package com.policy.engine.core;

import com.policy.engine.condition.ConditionEvaluator;
import com.policy.engine.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PolicyEngine {

    private static final Logger log = LoggerFactory.getLogger(PolicyEngine.class);

    private final PolicyDefinition policyDefinition;
    private final List<PolicyRule> sortedRules;
    private final AntPathMatcher pathMatcher;
    private final Map<String, CacheEntry> decisionCache;
    private final long cacheTtlMs;
    private final ConditionEvaluator conditionEvaluator;

    public PolicyEngine(PolicyDefinition policyDefinition) {
        this(policyDefinition, 60_000);
    }

    public PolicyEngine(PolicyDefinition policyDefinition, long cacheTtlMs) {
        this.policyDefinition = policyDefinition;
        this.pathMatcher = new AntPathMatcher();
        this.decisionCache = new ConcurrentHashMap<>();
        this.cacheTtlMs = cacheTtlMs;
        this.conditionEvaluator = new ConditionEvaluator();

        List<PolicyRule> rules = policyDefinition.getRules();
        if (rules == null) {
            this.sortedRules = Collections.emptyList();
        } else {
            this.sortedRules = rules.stream()
                    .sorted(Comparator.comparingInt(PolicyRule::getPriority))
                    .collect(Collectors.toList());
        }
        detectConflicts();
    }

    public boolean evaluate(AccessRequest request) {
        if (request == null) return false;

        String cacheKey = buildCacheKey(request);
        CacheEntry cached = decisionCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.granted;
        }

        String requestedResource = request.getResource();
        String requestedAction = request.getAction();
        boolean hasDenyRule = false;

        for (PolicyRule rule : sortedRules) {
            if (rule.getEffect() != Effect.DENY) continue;
            if (!matchAction(rule, requestedAction)) continue;
            if (!matchResource(rule.getResource(), requestedResource)) continue;
            if (!matchRoles(rule, request)) continue;
            if (!matchTenants(rule, request)) continue;
            if (!matchScopes(rule, request)) continue;
            if (!conditionEvaluator.evaluateConditions(rule.getConditions(), request)) continue;

            hasDenyRule = true;
            if (rule.isAudit()) {
                log.warn("DECISION=DENY | user={} resource={} action={} rule='{}' reason=DENY_RULE_MATCHED",
                        request.getSubject(), requestedResource, requestedAction, rule.getName());
            }
            decisionCache.put(cacheKey, new CacheEntry(false));
            return false;
        }

        for (PolicyRule rule : sortedRules) {
            if (rule.getEffect() != Effect.ALLOW) continue;
            if (!matchAction(rule, requestedAction)) continue;
            if (!matchResource(rule.getResource(), requestedResource)) continue;
            if (!matchRoles(rule, request)) continue;
            if (!matchTenants(rule, request)) continue;
            if (!matchScopes(rule, request)) continue;
            if (!conditionEvaluator.evaluateConditions(rule.getConditions(), request)) continue;

            if (rule.isAudit()) {
                log.info("DECISION=ALLOW | user={} resource={} action={} rule='{}'",
                        request.getSubject(), requestedResource, requestedAction, rule.getName());
            }
            decisionCache.put(cacheKey, new CacheEntry(true));
            return true;
        }

        if (policyDefinition.isDefaultDeny()) {
            log.warn("DECISION=DENY | user={} resource={} action={} reason=DEFAULT_DENY",
                    request.getSubject(), requestedResource, requestedAction);
        }
        decisionCache.put(cacheKey, new CacheEntry(false));
        return false;
    }

    private boolean matchAction(PolicyRule rule, String action) {
        return rule.getActions() == null || rule.getActions().isEmpty() || rule.getActions().contains(action);
    }

    private boolean matchResource(String ruleResource, String requestResource) {
        return pathMatcher.match(ruleResource, requestResource);
    }

    private boolean matchRoles(PolicyRule rule, AccessRequest request) {
        if (rule.getRoles() == null || rule.getRoles().isEmpty()) return true;
        Object roleObj = request.getContext().get("role");
        if (roleObj == null) return false;
        return rule.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase(roleObj.toString())
                || roleObj.toString().equalsIgnoreCase("ROLE_" + r));
    }

    private boolean matchTenants(PolicyRule rule, AccessRequest request) {
        if (rule.getTenants() == null || rule.getTenants().isEmpty()) return true;
        String tenantId = request.getTenantId();
        if (tenantId == null) return false;
        return rule.getTenants().contains(tenantId);
    }

    private boolean matchScopes(PolicyRule rule, AccessRequest request) {
        if (rule.getScopes() == null || rule.getScopes().isEmpty()) return true;
        Object scopeObj = request.getContext().get("scope");
        if (scopeObj == null) return false;
        String scope = scopeObj.toString();
        return rule.getScopes().stream().anyMatch(s -> s.equalsIgnoreCase(scope)
                || scope.startsWith(s));
    }

    public Map<String, String> extractRouteVariables(String pattern, String path) {
        return pathMatcher.extractUriTemplateVariables(pattern, path);
    }

    public void invalidateCache() { decisionCache.clear(); }

    public void invalidateCache(String user, String resource, String action) {
        decisionCache.remove(user + "|" + resource + "|" + action);
    }

    private String buildCacheKey(AccessRequest request) {
        return request.getSubject() + "|" + request.getResource() + "|" + request.getAction() + "|" + request.getTenantId();
    }

    private void detectConflicts() {
        Map<String, List<PolicyRule>> groups = new HashMap<>();
        for (PolicyRule rule : sortedRules) {
            for (String action : rule.getActions()) {
                String key = rule.getResource() + ":" + action;
                groups.computeIfAbsent(key, k -> new ArrayList<>()).add(rule);
            }
        }
        for (Map.Entry<String, List<PolicyRule>> entry : groups.entrySet()) {
            List<PolicyRule> group = entry.getValue();
            if (group.size() > 1) {
                List<PolicyRule> samePriority = group.stream()
                        .collect(Collectors.groupingBy(PolicyRule::getPriority))
                        .values().stream()
                        .filter(l -> l.size() > 1)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
                if (!samePriority.isEmpty()) {
                    log.warn("CONFLICT: resource/action={} has {} rules with same priority: {}",
                            entry.getKey(), samePriority.size(),
                            samePriority.stream().map(PolicyRule::getName).collect(Collectors.joining(", ")));
                }
            }
        }
    }

    public List<PolicyRule> getSortedRules() { return sortedRules; }
    public PolicyDefinition getPolicyDefinition() { return policyDefinition; }

    private static class CacheEntry {
        final boolean granted;
        final long timestamp;
        CacheEntry(boolean granted) { this.granted = granted; this.timestamp = System.currentTimeMillis(); }
        boolean isExpired() { return System.currentTimeMillis() - timestamp > cacheTtlMs; }
    }
}
