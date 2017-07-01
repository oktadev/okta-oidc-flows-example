package com.okta.oidcflows.controller;

import com.okta.oidcflows.config.TenantConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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

    @RequestMapping(value = "/is_changed", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> isChanged() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("isChanged", tenantConfig.anyChanged());

        return ret;
    }

    @RequestMapping(value = "/reset_config", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> resetConfig() {
        return tenantConfig.resetEnv();
    }
}
