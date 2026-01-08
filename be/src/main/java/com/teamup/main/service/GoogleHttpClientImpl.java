package com.teamup.main.service;

import org.springframework.stereotype.Component;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Form;

/**
 * Implementation thực tế của GoogleHttpClient
 * Sử dụng Apache HttpComponents để gọi Google APIs
 */
@Component
public class GoogleHttpClientImpl implements GoogleHttpClient {

    @Override
    public String postTokenRequest(String endpoint, String clientId, String clientSecret,
                                   String redirectUri, String code, String grantType) throws Exception {
        return Request.Post(endpoint)
                .bodyForm(
                        Form.form()
                                .add("client_id", clientId)
                                .add("client_secret", clientSecret)
                                .add("redirect_uri", redirectUri)
                                .add("code", code)
                                .add("grant_type", grantType)
                                .build())
                .execute().returnContent().asString();
    }

    @Override
    public String getUserInfo(String endpoint) throws Exception {
        return Request.Get(endpoint).execute().returnContent().asString();
    }
}
