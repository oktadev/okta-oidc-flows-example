package com.okta.oidcflows.controller;

import com.okta.oidcflows.config.TenantConfig;
import com.okta.oidcflows.util.DashedStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    TenantConfig tenantConfig;

    @RequestMapping("/")
    public String home(HttpServletRequest req, Model model) {
        model.addAttribute("oidcClientId", tenantConfig.getOidcClientId());
        model.addAttribute("authorizationServerId", tenantConfig.getAuthorizationServerId());
        model.addAttribute("oktaOrg", tenantConfig.getOktaOrg());
        model.addAttribute("nonce", UUID.randomUUID().toString());
        model.addAttribute("state", DashedStringGenerator.generate(4));
        model.addAttribute("redirectUri", tenantConfig.getRedirectUri(req));
        return "home";
    }

    @RequestMapping("/chart")
    public String chart() {
        return "chart";
    }
}
