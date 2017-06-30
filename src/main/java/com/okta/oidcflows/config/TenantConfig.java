package com.okta.oidcflows.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TenantConfig {

    // not updateable after startup
    @Value("#{ @environment['okta.oidc.client.secret'] }")
    protected String oidcClientSecret;


    @Value("#{ @environment['okta.oidc.client.id'] }")
    protected String oidcClientId;

    @Value("#{ @environment['okta.authorizationServer.id'] }")
    protected String authorizationServerId;

    @Value("#{ @environment['okta.org'] }")
    protected String oktaOrg;

    @Value("#{ @environment['redirect.uri'] }")
    protected String redirectUri;

    @Autowired
    Environment env;

    private static Map<String, String> envMap;

    @PostConstruct
    private void setup() {
        envMap = new HashMap<>();
        envMap.put("okta.oidc.client.id", oidcClientId);
        envMap.put("okta.authorizationServer.id", authorizationServerId);
        envMap.put("okta.org", oktaOrg);
    }

    // not updateable after startup
    public String getOidcClientSecret() {
        return oidcClientSecret;
    }

    // not updateable after startup
    public String getRedirectUri(HttpServletRequest req) {
        String proto = req.getHeader("x-forwarded-proto");
        String requestUrl = req.getRequestURL().toString();

        int finalSlashIdx = requestUrl.lastIndexOf("/");
        requestUrl = requestUrl.substring(0, finalSlashIdx);

        requestUrl = (proto != null) ?
            proto + "://" + requestUrl.substring(req.getRequestURL().indexOf("//")+2) :
            requestUrl;

        return requestUrl + redirectUri;
    }

    public String getOidcClientId() {
        return getEnv("okta.oidc.client.id");
    }

    public String getAuthorizationServerId() {
        return getEnv("okta.authorizationServer.id");
    }

    public String getOktaOrg() {
        return getEnv("okta.org");
    }

    public boolean isChanged(String envVar) {
        String envToCompare = envMap.get(envVar);
        if (envToCompare != null) {
            return !envToCompare.equals(env.getProperty(envVar));
        }
        return false;
    }

    public void updateConfig(String key, String value) {
        if (!envMap.containsKey(key)) { return; }
        envMap.put(key, value);
    }

    public Map<String, String> getEnv() {
        return envMap;
    }

    public String getEnv(String key) {
        return envMap.get(key);
    }
}
