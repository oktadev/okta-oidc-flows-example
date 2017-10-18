package com.okta.oidcflows.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Scope(value= WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TenantConfig {

    public static final String FLOW_REDIRECT_URI = "/flow_result";
    public static final String SESSION_REDIRECT_URI = "/continue";

    // BEGIN not updateable after startup
    @Value("#{ @environment['okta.oidc.client.secret'] }")
    protected String oidcClientSecret;

    @Value("#{ @environment['okta.session.username'] }")
    protected String sessionUsername;

    @Value("#{ @environment['okta.session.password'] }")
    protected String sessionPassword;
    // END not updateable after startup

    @Value("#{ @environment['okta.oidc.client.id'] }")
    protected String oidcClientId;

    @Value("#{ @environment['okta.authorization.server.id'] }")
    protected String authorizationServerId;

    @Value("#{ @environment['okta.org'] }")
    protected String oktaOrg;

    private Environment env;

    public TenantConfig(Environment env) {
        this.env = env;
    }

    private static Map<String, String> envMap;

    @PostConstruct
    private void setup() {
        envMap = new HashMap<>();
        envMap.put("okta.oidc.client.id", oidcClientId);
        envMap.put("okta.authorization.server.id", authorizationServerId);
        envMap.put("okta.org", oktaOrg);
    }

    // BEGIN not updateable after startup
    public String getOidcClientSecret() {
        return oidcClientSecret;
    }

    public String getSessionUsername() {
        return sessionUsername;
    }

    public String getSessionPassword() {
        return sessionPassword;
    }

    public String getRedirectUrl(HttpServletRequest req, String redirectUri) {
        String proto = req.getHeader("x-forwarded-proto");
        String requestUrl = req.getRequestURL().toString();

        int finalSlashIdx = requestUrl.lastIndexOf("/");
        requestUrl = requestUrl.substring(0, finalSlashIdx);

        requestUrl = (proto != null) ?
            proto + "://" + requestUrl.substring(req.getRequestURL().indexOf("//")+2) :
            requestUrl;

        return requestUrl + redirectUri;
    }
    // END not updateable after startup

    public String getOidcClientId() {
        return getEnv("okta.oidc.client.id");
    }

    public String getAuthorizationServerId() {
        return getEnv("okta.authorization.server.id");
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

    public boolean anyChanged() {
        for (String key : envMap.keySet()) {
            if (isChanged(key)) {
                return true;
            }
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

    public Map<String, String> resetEnv() {
        envMap.keySet().forEach(k -> envMap.put(k, env.getProperty(k)));
        return envMap;
    }
}
