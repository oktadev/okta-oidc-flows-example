package com.okta.oidcflows.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.oidcflows.config.TenantConfig;
import com.okta.oidcflows.service.OIDCService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CodeFlowController {

    @Autowired
    OIDCService oidcService;

    @Autowired
    TenantConfig tenantConfig;

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public String showCode(@RequestParam String code, @RequestParam String state, Model model) {
        model.addAttribute("code", code);
        model.addAttribute("state", state);

        return "show_code";
    }

    @RequestMapping(value = "/code", method = RequestMethod.POST)
    public String exchangeCode(@RequestParam String code, @RequestParam String state, Model model) throws IOException, AuthenticationException {

        model.addAttribute("result", oidcService.exchangeCode(code));
        return "exchange_code";
    }
}
