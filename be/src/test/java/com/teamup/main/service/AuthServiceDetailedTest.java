package com.teamup.main.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.google.gson.Gson;
import com.teamup.main.dto.request.GoogleAccount;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.exception.AppException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests cho AuthService
 * Covers OAuth2 flow, token validation, user info retrieval
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Comprehensive Coverage Tests")
public class AuthServiceDetailedTest {

    @Mock
    private GoogleHttpClient googleHttpClient;

    @InjectMocks
    private AuthService authService;

    // Constants
    private static final String CLIENT_ID = "test_client_id";
    private static final String CLIENT_SECRET = "test_client_secret";
    private static final String REDIRECT_URI = "http://localhost:3000/callback";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_ENDPOINT = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";
    private static final String ADMIN_EMAIL = "admin@teamup.com";

    @BeforeEach
    void setUp() {
        // Inject config values using ReflectionTestUtils
        ReflectionTestUtils.setField(authService, "googleClientId", CLIENT_ID);
        ReflectionTestUtils.setField(authService, "googleClientSecret", CLIENT_SECRET);
        ReflectionTestUtils.setField(authService, "googleRedirectUri", REDIRECT_URI);
        ReflectionTestUtils.setField(authService, "googleGrantType", GRANT_TYPE);
        ReflectionTestUtils.setField(authService, "googleLinkGetToken", TOKEN_ENDPOINT);
        ReflectionTestUtils.setField(authService, "googleLinkGetUserInfo", USER_INFO_ENDPOINT);
        ReflectionTestUtils.setField(authService, "googleAdminEmail", ADMIN_EMAIL);
    }

    // ===================== getToken Tests =====================

    @Test
    @DisplayName("Should get access token successfully from authorization code")
    void testGetTokenSuccess() throws Exception {
        // Arrange
        String code = "auth_code_12345";
        String jsonResponse = "{\"access_token\": \"access_token_xyz\", \"token_type\": \"Bearer\"}";
        when(googleHttpClient.postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET, 
                REDIRECT_URI, code, GRANT_TYPE)).thenReturn(jsonResponse);

        // Act
        String token = authService.getToken(code);

        // Assert
        assertEquals("access_token_xyz", token);
        verify(googleHttpClient, times(1)).postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID, 
                CLIENT_SECRET, REDIRECT_URI, code, GRANT_TYPE);
    }

    @Test
    @DisplayName("Should return null when response doesn't contain access_token")
    void testGetTokenNoAccessToken() throws Exception {
        // Arrange
        String code = "invalid_code";
        String jsonResponse = "{\"error\": \"invalid_code\", \"error_description\": \"Code is invalid\"}";
        when(googleHttpClient.postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET,
                REDIRECT_URI, code, GRANT_TYPE)).thenReturn(jsonResponse);

        // Act
        String token = authService.getToken(code);

        // Assert
        assertNull(token);
        verify(googleHttpClient, times(1)).postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID,
                CLIENT_SECRET, REDIRECT_URI, code, GRANT_TYPE);
    }

    @Test
    @DisplayName("Should throw AppException when token endpoint fails")
    void testGetTokenHttpError() throws Exception {
        // Arrange
        String code = "auth_code";
        when(googleHttpClient.postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET,
                REDIRECT_URI, code, GRANT_TYPE)).thenThrow(new Exception("Network error"));

        // Act & Assert
        assertThrows(Exception.class, () -> authService.getToken(code));
        verify(googleHttpClient, times(1)).postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID,
                CLIENT_SECRET, REDIRECT_URI, code, GRANT_TYPE);
    }

    @Test
    @DisplayName("Should parse access token from complex JSON response")
    void testGetTokenComplexResponse() throws Exception {
        // Arrange
        String code = "auth_code_complex";
        String jsonResponse = "{\"access_token\": \"token_abc123\", \"token_type\": \"Bearer\", " +
                "\"expires_in\": 3599, \"scope\": \"openid email profile\"}";
        when(googleHttpClient.postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET,
                REDIRECT_URI, code, GRANT_TYPE)).thenReturn(jsonResponse);

        // Act
        String token = authService.getToken(code);

        // Assert
        assertEquals("token_abc123", token);
    }

    // ===================== getUserInfo Tests =====================

    @Test
    @DisplayName("Should retrieve user info successfully from access token")
    void testGetUserInfoSuccess() throws Exception {
        // Arrange
        String accessToken = "access_token_xyz";
        String userInfoJson = "{\"id\": \"123456789\", \"email\": \"user@gmail.com\", " +
                "\"name\": \"Test User\", \"picture\": \"http://example.com/pic.jpg\", " +
                "\"verified_email\": true}";
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + accessToken))
                .thenReturn(userInfoJson);

        // Act
        GoogleAccount account = authService.getUserInfo(accessToken);

        // Assert
        assertNotNull(account);
        assertEquals("user@gmail.com", account.getEmail());
        assertEquals("123456789", account.getId());
        assertTrue(account.isVerified_email());
        verify(googleHttpClient, times(1)).getUserInfo(USER_INFO_ENDPOINT + accessToken);
    }

    @Test
    @DisplayName("Should throw AppException when getUserInfo fails")
    void testGetUserInfoFails() throws Exception {
        // Arrange
        String accessToken = "invalid_token";
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + accessToken))
                .thenThrow(new Exception("Invalid token"));

        // Act & Assert
        AppException exception = assertThrows(AppException.class, 
                () -> authService.getUserInfo(accessToken));
        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should parse all user info fields correctly")
    void testGetUserInfoAllFields() throws Exception {
        // Arrange
        String accessToken = "token_123";
        String userInfoJson = "{\"id\": \"987654321\", \"email\": \"testuser@gmail.com\", " +
                "\"name\": \"Full Name\", \"picture\": \"http://example.com/avatar.jpg\", " +
                "\"locale\": \"en\", \"verified_email\": true}";
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + accessToken))
                .thenReturn(userInfoJson);

        // Act
        GoogleAccount account = authService.getUserInfo(accessToken);

        // Assert
        assertEquals("987654321", account.getId());
        assertEquals("testuser@gmail.com", account.getEmail());
        assertEquals("Full Name", account.getName());
        assertTrue(account.isVerified_email());
    }

    // ===================== verifyAccessToken Tests =====================

    @Test
    @DisplayName("Should verify token and return 'User' for regular user")
    void testVerifyAccessTokenRegularUser() throws Exception {
        // Arrange
        String accessToken = "valid_token";
        String userInfoJson = "{\"id\": \"123\", \"email\": \"user@gmail.com\", " +
                "\"verified_email\": true}";
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + accessToken))
                .thenReturn(userInfoJson);

        // Act
        ApiResponse<Boolean> response = authService.verifyAccessToken(accessToken);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("User", response.getMessage());
        assertTrue(response.getResult());
        verify(googleHttpClient, times(1)).getUserInfo(USER_INFO_ENDPOINT + accessToken);
    }

    @Test
    @DisplayName("Should verify token and return 'Admin' for admin user")
    void testVerifyAccessTokenAdminUser() throws Exception {
        // Arrange
        String accessToken = "admin_token";
        String userInfoJson = "{\"id\": \"admin123\", \"email\": \"admin@teamup.com\", " +
                "\"verified_email\": true}";
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + accessToken))
                .thenReturn(userInfoJson);

        // Act
        ApiResponse<Boolean> response = authService.verifyAccessToken(accessToken);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("Admin", response.getMessage());
        assertTrue(response.getResult());
    }

    @Test
    @DisplayName("Should return 401 when email is not verified")
    void testVerifyAccessTokenUnverifiedEmail() throws Exception {
        // Arrange
        String accessToken = "unverified_token";
        String userInfoJson = "{\"id\": \"456\", \"email\": \"user@gmail.com\", " +
                "\"verified_email\": false}";
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + accessToken))
                .thenReturn(userInfoJson);

        // Act
        ApiResponse<Boolean> response = authService.verifyAccessToken(accessToken);

        // Assert
        assertNotNull(response);
        assertEquals(401, response.getCode());
        assertFalse(response.getResult());
        assertTrue(response.getMessage().contains("verified_email=false"));
    }

    @Test
    @DisplayName("Should throw INTERNET_ERROR for network issues")
    void testVerifyAccessTokenNetworkError() throws Exception {
        // Arrange
        String accessToken = "token_network";
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + accessToken))
                .thenThrow(new org.apache.http.client.ClientProtocolException("Connection refused"));

        // Act & Assert
        AppException exception = assertThrows(AppException.class,
                () -> authService.verifyAccessToken(accessToken));
        assertEquals(ErrorCode.INTERNET_ERROR, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw BAD_GATEWAY for other errors")
    void testVerifyAccessTokenBadGateway() throws Exception {
        // Arrange
        String accessToken = "token_error";
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + accessToken))
                .thenThrow(new Exception("Gateway timeout"));

        // Act & Assert
        AppException exception = assertThrows(AppException.class,
                () -> authService.verifyAccessToken(accessToken));
        assertEquals(ErrorCode.BAD_GATEWAY, exception.getErrorCode());
    }

    // ===================== Edge Cases =====================

    @Test
    @DisplayName("Should handle empty authorization code")
    void testGetTokenEmptyCode() throws Exception {
        // Arrange
        String code = "";
        String jsonResponse = "{\"error\": \"invalid_request\"}";
        when(googleHttpClient.postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET,
                REDIRECT_URI, code, GRANT_TYPE)).thenReturn(jsonResponse);

        // Act
        String token = authService.getToken(code);

        // Assert
        assertNull(token);
    }

    @Test
    @DisplayName("Should handle missing access token in response")
    void testGetTokenMissingAccessToken() throws Exception {
        // Arrange
        String code = "code";
        String jsonResponse = "{\"error\": \"unsupported_grant_type\"}";
        when(googleHttpClient.postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET,
                REDIRECT_URI, code, GRANT_TYPE)).thenReturn(jsonResponse);

        // Act
        String token = authService.getToken(code);

        // Assert
        assertNull(token);
    }

    @Test
    @DisplayName("Should construct correct user info endpoint URL")
    void testUserInfoUrlConstruction() throws Exception {
        // Arrange
        String accessToken = "token_url_test";
        String expectedUrl = USER_INFO_ENDPOINT + accessToken;
        String userInfoJson = "{\"id\": \"123\", \"email\": \"test@gmail.com\", \"verified_email\": true}";
        when(googleHttpClient.getUserInfo(expectedUrl)).thenReturn(userInfoJson);

        // Act
        GoogleAccount account = authService.getUserInfo(accessToken);

        // Assert
        assertNotNull(account);
        verify(googleHttpClient, times(1)).getUserInfo(expectedUrl);
    }

    @Test
    @DisplayName("Should verify correct parameters passed to token endpoint")
    void testTokenEndpointParameters() throws Exception {
        // Arrange
        String code = "param_test_code";
        String jsonResponse = "{\"access_token\": \"token\"}";
        when(googleHttpClient.postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET,
                REDIRECT_URI, code, GRANT_TYPE)).thenReturn(jsonResponse);

        // Act
        authService.getToken(code);

        // Assert
        verify(googleHttpClient, times(1)).postTokenRequest(
                TOKEN_ENDPOINT,
                CLIENT_ID,
                CLIENT_SECRET,
                REDIRECT_URI,
                code,
                GRANT_TYPE
        );
    }

    @Test
    @DisplayName("Should distinguish between admin and non-admin users correctly")
    void testAdminUserDetection() throws Exception {
        // Arrange
        String adminToken = "admin_token_test";
        String regularToken = "regular_token_test";
        
        String adminJson = "{\"id\": \"admin_id\", \"email\": \"admin@teamup.com\", \"verified_email\": true}";
        String userJson = "{\"id\": \"user_id\", \"email\": \"user@gmail.com\", \"verified_email\": true}";
        
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + adminToken)).thenReturn(adminJson);
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + regularToken)).thenReturn(userJson);

        // Act
        ApiResponse<Boolean> adminResponse = authService.verifyAccessToken(adminToken);
        ApiResponse<Boolean> userResponse = authService.verifyAccessToken(regularToken);

        // Assert
        assertEquals("Admin", adminResponse.getMessage());
        assertEquals("User", userResponse.getMessage());
    }

    @Test
    @DisplayName("Should handle token with special characters")
    void testTokenWithSpecialCharacters() throws Exception {
        // Arrange
        String code = "code_with_special_!@#$%";
        String jsonResponse = "{\"access_token\": \"token_!@#$%^&*()\"}";
        when(googleHttpClient.postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET,
                REDIRECT_URI, code, GRANT_TYPE)).thenReturn(jsonResponse);

        // Act
        String token = authService.getToken(code);

        // Assert
        assertEquals("token_!@#$%^&*()", token);
    }

    @Test
    @DisplayName("Should handle very long access tokens")
    void testLongAccessToken() throws Exception {
        // Arrange
        String longToken = "a".repeat(2000);
        String userInfoJson = "{\"id\": \"123\", \"email\": \"test@gmail.com\", \"verified_email\": true}";
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + longToken))
                .thenReturn(userInfoJson);

        // Act
        GoogleAccount account = authService.getUserInfo(longToken);

        // Assert
        assertNotNull(account);
        verify(googleHttpClient, times(1)).getUserInfo(USER_INFO_ENDPOINT + longToken);
    }

    @Test
    @DisplayName("Integration: Full OAuth2 flow simulation")
    void testFullOAuth2Flow() throws Exception {
        // Arrange - Step 1: Get token
        String authCode = "auth_code_integration";
        String tokenResponse = "{\"access_token\": \"flow_token_123\", \"token_type\": \"Bearer\", \"expires_in\": 3599}";
        when(googleHttpClient.postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET,
                REDIRECT_URI, authCode, GRANT_TYPE)).thenReturn(tokenResponse);

        // Act - Step 1: Exchange code for token
        String accessToken = authService.getToken(authCode);

        // Arrange - Step 2: Get user info
        String userInfoResponse = "{\"id\": \"flow_user_123\", \"email\": \"flowtest@gmail.com\", " +
                "\"name\": \"Flow Test\", \"verified_email\": true}";
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + accessToken))
                .thenReturn(userInfoResponse);

        // Act - Step 2: Get user info
        GoogleAccount userInfo = authService.getUserInfo(accessToken);

        // Arrange - Step 3: Verify token
        when(googleHttpClient.getUserInfo(USER_INFO_ENDPOINT + accessToken))
                .thenReturn(userInfoResponse);

        // Act - Step 3: Verify token
        ApiResponse<Boolean> verifyResponse = authService.verifyAccessToken(accessToken);

        // Assert - Verify entire flow
        assertNotNull(accessToken);
        assertEquals("flow_token_123", accessToken);
        assertNotNull(userInfo);
        assertEquals("flowtest@gmail.com", userInfo.getEmail());
        assertTrue(verifyResponse.getResult());
        assertEquals(200, verifyResponse.getCode());
    }

    @Test
    @DisplayName("Should properly handle JSON parsing errors")
    void testInvalidJsonResponse() throws Exception {
        // Arrange
        String code = "code_invalid_json";
        String invalidJson = "{invalid json response}";
        when(googleHttpClient.postTokenRequest(TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET,
                REDIRECT_URI, code, GRANT_TYPE)).thenReturn(invalidJson);

        // Act & Assert
        assertThrows(Exception.class, () -> authService.getToken(code));
    }
}
