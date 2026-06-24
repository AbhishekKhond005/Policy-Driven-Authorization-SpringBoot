package com.policy.gateway.forward;

import com.policy.gateway.route.RouteMapping;
import com.policy.gateway.route.RouteRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

@Component
public class RequestForwarder {
    private static final Logger log = LoggerFactory.getLogger(RequestForwarder.class);
    private final RouteRegistry routeRegistry;

    public RequestForwarder(RouteRegistry routeRegistry) {
        this.routeRegistry = routeRegistry;
    }

    public void forward(HttpServletRequest request, HttpServletResponse response,
                        RouteMapping route) throws IOException {
        String targetUrl = buildTargetUrl(request, route);
        log.info("Forwarding {} {} -> {}", request.getMethod(), request.getRequestURI(), targetUrl);

        URL url = URI.create(targetUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(request.getMethod());
        conn.setConnectTimeout(route.getTimeoutMs());
        conn.setReadTimeout(route.getTimeoutMs());
        conn.setInstanceFollowRedirects(false);

        // Copy request headers
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            if (!header.equalsIgnoreCase("Host")) {
                conn.setRequestProperty(header, request.getHeader(header));
            }
        }
        conn.setRequestProperty("X-Forwarded-For", request.getRemoteAddr());
        conn.setRequestProperty("X-Forwarded-Proto", request.getScheme());

        // Copy request body for POST/PUT
        if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream();
                 InputStream is = request.getInputStream()) {
                is.transferTo(os);
            }
        }

        conn.connect();

        // Copy response
        response.setStatus(conn.getResponseCode());
        conn.getHeaderFields().forEach((key, values) -> {
            if (key != null && !key.equalsIgnoreCase("Transfer-Encoding")) {
                values.forEach(value -> response.addHeader(key, value));
            }
        });

        try (InputStream is = conn.getResponseCode() >= 400
                ? conn.getErrorStream() : conn.getInputStream()) {
            if (is != null) {
                is.transferTo(response.getOutputStream());
            }
        }
    }

    private String buildTargetUrl(HttpServletRequest request, RouteMapping route) {
        String targetBase = route.getTargetUrl();
        if (route.isStripPrefix()) {
            String remainingPath = request.getRequestURI();
            targetBase += remainingPath;
        }
        if (request.getQueryString() != null) {
            targetBase += "?" + request.getQueryString();
        }
        return targetBase;
    }
}
