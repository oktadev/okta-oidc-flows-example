package com.okta.oidcflows.controller;

import com.okta.oidcflows.service.OIDCService;
import org.apache.http.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class ValidateController {

    @Autowired
    OIDCService oidcService;

    @RequestMapping("/userinfo")
    @ResponseBody Map<String, Object> userinfo(@RequestBody Map<String, String> requestParams) throws IOException {
        return oidcService.userInfo(requestParams.get("access_token"));
    }

    @RequestMapping("/introspect")
    @ResponseBody Map<String, Object> introspect(@RequestBody Map<String, String> requestParams) throws IOException, AuthenticationException {
        return oidcService.introspect(requestParams.get("access_token"));
    }

    @RequestMapping("/validate")
    @ResponseBody Map<String, Object> validate(@RequestBody Map<String, String> requestParams) throws IOException {
        return oidcService.validate(requestParams.get("id_token"));
    }
}
