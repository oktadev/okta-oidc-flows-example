package com.okta.oidcflows.service;

import org.apache.http.auth.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface OIDCService {

    Map<String, Object> exchangeCode(HttpServletRequest req, String code) throws IOException, AuthenticationException;
    boolean validateIdToken(String idToken);
    Map<String, Object> userInfo(String accessToken) throws IOException;
    Map<String, Object> introspect(String accessToken) throws IOException, AuthenticationException;
    Map<String, Object> validate(String idToken) throws IOException;
}
