package com.okta.oidcflows.controller;

import com.okta.oidcflows.config.TenantConfig;
import com.okta.oidcflows.service.OIDCService;
import org.apache.http.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class FlowTypeController {

    @Autowired
    OIDCService oidcService;

    @Autowired
    TenantConfig tenantConfig;

    @RequestMapping(value = TenantConfig.REDIRECT_URI, method = RequestMethod.GET)
    public String showCode(HttpServletRequest req) {
        return "flow_results";
    }

    @RequestMapping(value = "/exchange_code", method = RequestMethod.POST)
    public String exchangeCode(HttpServletRequest req, @RequestParam Map<String, String> requestParams)  throws IOException, AuthenticationException {
        String code = requestParams.get("code");
        String state =  requestParams.get("state");
        Map<String, Object> codeResult = oidcService.exchangeCode(req, code);

        String results = "state=" + state;
        results += codeResult
            .entrySet()
            .stream()
            .map(entry -> "&" + entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining());

        return "redirect:" + TenantConfig.REDIRECT_URI + "?" + results;
    }

}
