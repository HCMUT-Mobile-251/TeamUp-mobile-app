package com.teamup.main.service;

/**
 * Interface để wrap các HTTP calls tới Google API
 * Giúp dễ dàng mock trong tests
 */
public interface GoogleHttpClient {
    /**
     * Gửi POST request tới Google token endpoint
     * @param endpoint URL của endpoint
     * @param clientId Google Client ID
     * @param clientSecret Google Client Secret
     * @param redirectUri Redirect URI
     * @param code Authorization code từ Google
     * @param grantType Grant type (authorization_code)
     * @return JSON response từ Google
     * @throws Exception nếu request thất bại
     */
    String postTokenRequest(String endpoint, String clientId, String clientSecret, 
                           String redirectUri, String code, String grantType) throws Exception;

    /**
     * Gửi GET request tới Google user info endpoint
     * @param endpoint URL của endpoint kèm access token
     * @return JSON response từ Google
     * @throws Exception nếu request thất bại
     */
    String getUserInfo(String endpoint) throws Exception;
}
