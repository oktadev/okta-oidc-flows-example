package com.okta.oidcflows.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.oidcflows.config.TenantConfig;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OIDCServiceImpl implements OIDCService {


    @Autowired
    TenantConfig tenantConfig;

    @Override
    public Map<String, String> exchangeCode(String code) throws IOException, AuthenticationException {
        HttpPost httpPost = new HttpPost(
            "https://" + tenantConfig.getOktaOrg() + "/oauth2/" +
            tenantConfig.getAuthorizationServerId() + "/v1/token"
        );
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nvps.add(new BasicNameValuePair("redirect_uri", tenantConfig.getRedirectUri()));
        nvps.add(new BasicNameValuePair("code", code));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        UsernamePasswordCredentials creds =
            new UsernamePasswordCredentials(tenantConfig.getOidcClientId(), tenantConfig.getOidcClientSecret());
        httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpResponse response = httpclient.execute(httpPost);

        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> result = mapper.readValue(response.getEntity().getContent(), typeRef);

        // introspect
        httpPost = new HttpPost(
            "https://" + tenantConfig.getOktaOrg()+ "/oauth2/" +
            tenantConfig.getAuthorizationServerId() + "/v1/introspect"
        );
        httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("token", result.get("access_token")));
        nvps.add(new BasicNameValuePair("token_type_hint", "access_token"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        response = httpclient.execute(httpPost);
        Map<String, Object> validation = mapper.readValue(response.getEntity().getContent(), typeRef);

        return result;
    }

    @Override
    public boolean validateIdToken(String idToken) {
        return false;
    }
}
