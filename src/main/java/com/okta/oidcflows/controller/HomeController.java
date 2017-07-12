package com.okta.oidcflows.controller;

import com.okta.oidcflows.config.TenantConfig;
import com.okta.oidcflows.service.OIDCService;
import com.okta.oidcflows.util.DashedStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    TenantConfig tenantConfig;

    @Autowired
    OIDCService oidcService;

    @RequestMapping("/")
    public String establishSession(HttpServletRequest req) throws IOException {
        return "redirect:" + oidcService.getSessionRedirectUrl(req);
    }

    @RequestMapping(TenantConfig.SESSION_REDIRECT_URI)
    public String home(HttpServletRequest req, Model model) {
        model.addAttribute("oidcClientId", tenantConfig.getOidcClientId());
        model.addAttribute("authorizationServerId", tenantConfig.getAuthorizationServerId());
        model.addAttribute("oktaOrg", tenantConfig.getOktaOrg());
        model.addAttribute("nonce", UUID.randomUUID().toString());
        model.addAttribute("state", DashedStringGenerator.generate(4));
        model.addAttribute("redirectUri", tenantConfig.getRedirectUrl(req, TenantConfig.FLOW_REDIRECT_URI));

        return "home";
    }

    @RequestMapping("/chart")
    public String chart() {
        return "chart";
    }
}
