package com.teamup.main.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.teamup.main.dto.request.GoogleAccount;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.exception.AppException;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthService {
    @Value("${google.GOOGLE_CLIENT_ID}")
    String googleClientId;

    @Value("${google.GOOGLE_CLIENT_SECRET}")
    String googleClientSecret;

    @Value("${google.GOOGLE_REDIRECT_URI}")
    String googleRedirectUri;

    @Value("${google.GOOGLE_GRANT_TYPE}")
    String googleGrantType;

    @Value("${google.GOOGLE_LINK_GET_TOKEN}")
    String googleLinkGetToken;

    @Value("${google.GOOGLE_LINK_GET_USER_INFO}")
    String googleLinkGetUserInfo;

    @Value("${google.ADMIN_EMAIL}")
    String googleAdminEmail;

    final Gson gson = new Gson();

    // fe gửi code và be gửi cho GG
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

        JsonObject jobj = gson.fromJson(response, JsonObject.class);
        return jobj.has("access_token") ? jobj.get("access_token").getAsString() : null;
    }

    // be nhận được token tkgg và gửi token tới gg lấy infor
    public GoogleAccount getUserInfo(final String accessToken) {
        try {
            String uri = this.googleLinkGetUserInfo + accessToken;

            String response = Request.Get(uri).execute().returnContent().asString();
            GoogleAccount googlePojo = gson.fromJson(response, GoogleAccount.class);
            return googlePojo;
        } catch (IOException e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public ApiResponse<Boolean> verifyAccessToken(String accessToken) {
        try {
            String uri = this.googleLinkGetUserInfo + accessToken;
            String response = Request.Get(uri).execute().returnContent().asString();

            GoogleAccount json = gson.fromJson(response, GoogleAccount.class);
            if (!json.isVerified_email()) {
                return ApiResponse.<Boolean>builder()
                        .code(401)
                        .message("Token không hợp lệ hoặc hết hạn: verified_email=" + json.isVerified_email())
                        .result(json.isVerified_email())
                        .build();
            }
            // message đặc biệt
            return ApiResponse.<Boolean>builder()
                    .code(200)
                    .message(this.googleAdminEmail.equals(json.getEmail()) ? "Admin" : "User")
                    .result(true)
                    .build();
        } catch (ClientProtocolException e) {
            throw new AppException(ErrorCode.INTERNET_ERROR);
        } catch (IOException e) {
            throw new AppException(ErrorCode.BAD_GATEWAY);
        }
    }
}
