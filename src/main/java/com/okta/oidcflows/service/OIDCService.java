package com.okta.oidcflows.service;

import org.apache.http.auth.AuthenticationException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface OIDCService {

    Map<String, String> exchangeCode(String code) throws IOException, AuthenticationException;
    boolean validateIdToken(String idToken);
}
