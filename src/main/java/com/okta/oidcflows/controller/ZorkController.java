package com.okta.oidcflows.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ZorkController {

    @Value("#{ @environment['okta.zork.url'] }")
    String oktaZorkUrl;

    @RequestMapping("/zork")
    public String zork(Model model) {
        model.addAttribute("oktaZorkUrl", oktaZorkUrl);
        return "zork";
    }
}
