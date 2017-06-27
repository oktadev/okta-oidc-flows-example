package com.okta.oidcflows.controller;

import com.okta.oidcflows.config.TenantConfig;
import com.okta.oidcflows.service.OIDCService;
import org.apache.http.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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


    @RequestMapping(value = "/code", method = RequestMethod.POST)
    public String exchangeCode(@RequestParam String code, @RequestParam String state, Model model) throws IOException, AuthenticationException {

        model.addAttribute("result", oidcService.exchangeCode(code));
        return "exchange_code";
    }
}
