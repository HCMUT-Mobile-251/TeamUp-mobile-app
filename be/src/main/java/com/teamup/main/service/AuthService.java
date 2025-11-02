package com.teamup.main.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.teamup.main.dto.response.GoogleAccount;

@Service
public class AuthService {
    @Value("${google.GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${google.GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    @Value("${google.GOOGLE_REDIRECT_URI}")
    private String googleRedirectUri;

    @Value("${google.GOOGLE_GRANT_TYPE}")
    private String googleGrantType;

    @Value("${google.GOOGLE_LINK_GET_TOKEN}")
    private String googleLinkGetToken;

    @Value("${google.GOOGLE_LINK_GET_USER_INFO}")
    private String googleLinkGetUserInfo;

    // <-- non-static so @Value fields are available
    public String getToken(String code) throws IOException {
        String response = Request.Post(this.googleLinkGetToken)
                .bodyForm(
                        Form.form()
                                .add("client_id", this.googleClientId)
                                .add("client_secret", this.googleClientSecret)
                                .add("redirect_uri", this.googleRedirectUri)
                                .add("code", code)
                                .add("grant_type", this.googleGrantType)
                                .build())
                .execute().returnContent().asString();

        JsonObject jobj = new Gson().fromJson(response, JsonObject.class);
        return jobj.has("access_token") ? jobj.get("access_token").getAsString() : null;
    }

    public GoogleAccount getUserInfo(final String accessToken) throws ClientProtocolException, IOException {
        String link = this.googleLinkGetUserInfo + accessToken;

        String response = Request.Get(link).execute().returnContent().asString();
        GoogleAccount googlePojo = new Gson().fromJson(response, GoogleAccount.class);
        System.out.println("Google Account Info: " + String.valueOf(googlePojo));
        return googlePojo;
    }
}
