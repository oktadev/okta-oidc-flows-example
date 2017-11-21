package com.okta.oidcflows.controller;

import com.okta.oidcflows.config.TenantConfig;
import com.okta.oidcflows.service.OIDCService;
import org.apache.http.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class FlowTypeController {

    @Value("#{ @environment['okta.zork.url'] }")
    private String oktaZorkUrl;

    private TenantConfig tenantConfig;
    private OIDCService oidcService;

    public FlowTypeController(TenantConfig tenantConfig, OIDCService oidcService) {
        this.tenantConfig = tenantConfig;
        this.oidcService = oidcService;
    }

    @RequestMapping(value = TenantConfig.FLOW_REDIRECT_URI, method = RequestMethod.GET)
    public String showCode(Model model) {
        model.addAttribute("oktaZorkUrl", oktaZorkUrl);
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

        return "redirect:" + TenantConfig.FLOW_REDIRECT_URI + "?" + results;
    }

}
