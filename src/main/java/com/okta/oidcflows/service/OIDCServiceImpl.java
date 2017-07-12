package com.okta.oidcflows.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.oidcflows.config.TenantConfig;
import com.okta.oidcflows.util.DashedStringGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OIDCServiceImpl implements OIDCService {


    @Autowired
    TenantConfig tenantConfig;

    private ObjectMapper mapper = new ObjectMapper();
    private CloseableHttpClient httpclient = HttpClients.createDefault();
    private TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};

    @Override
    public String getSessionRedirectUrl(HttpServletRequest req) throws IOException {

        Map<String, String> map = new HashMap<>();
        map.put("username", tenantConfig.getSessionUsername());
        map.put("password", tenantConfig.getSessionPassword());

        HttpResponse response = Request.Post("https://" + tenantConfig.getOktaOrg() + "/api/v1/authn")
            .addHeader("Content-type", "application/json")
            .body(new StringEntity(mapper.writeValueAsString(map)))
            .execute()
            .returnResponse();

        map = mapper.readValue(response.getEntity().getContent(), typeRef);
        String sessionToken = map.get("sessionToken");

        String sessionLink = "https://" + tenantConfig.getOktaOrg() +
            "/oauth2/" + tenantConfig.getAuthorizationServerId() + "/v1/authorize?" +
            "client_id=" + tenantConfig.getOidcClientId() + "&" +
            "response_type=code&scope=openid&" +
            "state=" + DashedStringGenerator.generate(4) + "&" +
            "nonce=" + UUID.randomUUID().toString() + "&" +
            "redirect_uri=" + tenantConfig.getRedirectUrl(req, TenantConfig.SESSION_REDIRECT_URI) + "&" +
            "sessionToken=" + sessionToken;

        return sessionLink;
    }

    @Override
    public Map<String, Object> exchangeCode(HttpServletRequest req, String code) throws IOException, AuthenticationException {
        HttpPost httpPost = new HttpPost(
            "https://" + tenantConfig.getOktaOrg() + "/oauth2/" +
            tenantConfig.getAuthorizationServerId() + "/v1/token"
        );
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nvps.add(new BasicNameValuePair("redirect_uri", tenantConfig.getRedirectUrl(req, TenantConfig.FLOW_REDIRECT_URI)));
        nvps.add(new BasicNameValuePair("code", code));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        UsernamePasswordCredentials creds =
            new UsernamePasswordCredentials(tenantConfig.getOidcClientId(), tenantConfig.getOidcClientSecret());
        httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

        HttpResponse response = httpclient.execute(httpPost);

        Map<String, Object> result = mapper.readValue(response.getEntity().getContent(), typeRef);

        return result;
    }

    @Override
    public boolean validateIdToken(String idToken) {
        return false;
    }

    @Override
    public Map<String, Object> userInfo(String accessToken) throws IOException {
        String userinfoLink = "https://" + tenantConfig.getOktaOrg() +
            "/oauth2/" + tenantConfig.getAuthorizationServerId() + "/v1/userinfo";

        HttpGet httpGet = new HttpGet(userinfoLink);
        httpGet.addHeader("Authorization", "Bearer " + accessToken);

        HttpResponse response = httpclient.execute(httpGet);

        Map<String, Object> rsp = mapper.readValue(response.getEntity().getContent(), typeRef);

        Map<String, Object> ret = new HashMap<>();
        ret.put("userinfoResponse", rsp);
        ret.put("userinfoLink", userinfoLink);

        return ret;
    }

    @Override
    public Map<String, Object> introspect(String accessToken) throws IOException, AuthenticationException {

        UsernamePasswordCredentials creds =
            new UsernamePasswordCredentials(tenantConfig.getOidcClientId(), tenantConfig.getOidcClientSecret());

        String introspectLink = "https://" + tenantConfig.getOktaOrg()+ "/oauth2/" +
            tenantConfig.getAuthorizationServerId() + "/v1/introspect";

        HttpPost httpPost = new HttpPost(introspectLink);
        httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("token", accessToken));
        nvps.add(new BasicNameValuePair("token_type_hint", "access_token"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        HttpResponse response = httpclient.execute(httpPost);
        Map<String, Object> validation = mapper.readValue(response.getEntity().getContent(), typeRef);

        Map<String, Object> ret = new HashMap<>();
        ret.put("introspectResponse", validation);
        ret.put("introspectLink", introspectLink);

        return ret;
    }

    public Map<String, Object> validate(String idToken) throws IOException {
        String jwksLink = "https://" + tenantConfig.getOktaOrg()+ "/oauth2/" +
            tenantConfig.getAuthorizationServerId() + "/v1/keys";

        SigningKeyResolver resolver = new SigningKeyResolverAdapter() {
            public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
                try {
                    Map<String, String> jwk = getKeyById(getJwks(jwksLink), jwsHeader.getKeyId());
                    BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(jwk.get("n")));
                    BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(jwk.get("e")));

                    return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };

        Jws<Claims> jwsClaims = Jwts.parser()
            .setSigningKeyResolver(resolver)
            .parseClaimsJws(idToken);

        Map<String, Object> ret = new HashMap<>();
        ret.put("validateResponse", jwsClaims.getBody());
        ret.put("jwksLink", jwksLink);

        return ret;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getKeyById(Map<String, Object> jwks, String kid) {
        List<Map<String, String>> keys = (List<Map<String, String>>)jwks.get("keys");
        Map<String, String> ret = null;
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).get("kid").equals(kid)) {
                return keys.get(i);
            }
        }
        return ret;
    }

    private Map<String, Object> getJwks(String jwksLink) throws IOException {
        HttpGet httpGet = new HttpGet(jwksLink);

        HttpResponse response = httpclient.execute(httpGet);

        return mapper.readValue(response.getEntity().getContent(), typeRef);
    }
}
