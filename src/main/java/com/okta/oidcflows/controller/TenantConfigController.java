package com.okta.oidcflows.controller;

import com.okta.oidcflows.config.TenantConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TenantConfigController {

    @Autowired
    TenantConfig tenantConfig;

    @RequestMapping(value = "/get_config", method = RequestMethod.GET)
    public Map<String, String> getConfig() {
        return tenantConfig.getEnv();
    }

    @RequestMapping(value = "/update_config", method = RequestMethod.PUT)
    public @ResponseBody Map<String, String> updateConfig(@RequestBody Map<String, String> envVars) {
        envVars.forEach((key, value) -> tenantConfig.updateConfig(key, value));
        return tenantConfig.getEnv();
    }

}
