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

    @RequestMapping(value = {"/code", "/implicit", "/hybrid"}, method = RequestMethod.GET)
    public String showCode(HttpServletRequest req) {
        return "flow_results";
    }


    @RequestMapping(value = "/dataAction", method = RequestMethod.POST)
    public String dataAction(@RequestParam Map<String, String> requestParams) throws IOException, AuthenticationException {

        String dataAction = requestParams.get("dataAction");
        if ("exchange-code".equals(dataAction)) {
            return exchangeCode(requestParams);
        }
        return null;
    }

    private String exchangeCode(Map<String, String> requestParams)  throws IOException, AuthenticationException {
        String code = requestParams.get("code");
        String state =  requestParams.get("state");
        Map<String, Object> codeResult = oidcService.exchangeCode(code);

        String results = "state=" + state;
        results += codeResult
                .entrySet()
                .stream()
                .map(entry -> "&" + entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining());

        return "redirect:/code?" + results;
    }

}
