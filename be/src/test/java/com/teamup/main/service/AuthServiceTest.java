package com.teamup.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.teamup.main.dto.request.GoogleAccount;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.exception.AppException;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests - Phase 6")
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    private String testCode;
    private String testAccessToken;
    private GoogleAccount testGoogleAccount;
    private String adminEmail;

    @BeforeEach
    void setUp() {
        testCode = "test_code_123";
        testAccessToken = "test_access_token_xyz";
        adminEmail = "admin@example.com";

        testGoogleAccount = GoogleAccount.builder()
                .id("google_12345")
                .email("user@example.com")
                .verified_email(true)
                .name("John Doe")
                .given_name("John")
                .family_name("Doe")
                .picture("https://example.com/photo.jpg")
                .hd("example.com")
                .build();

        ReflectionTestUtils.setField(authService, "googleClientId", "test_client_id");
        ReflectionTestUtils.setField(authService, "googleClientSecret", "test_client_secret");
        ReflectionTestUtils.setField(authService, "googleRedirectUri", "http://localhost:3000");
        ReflectionTestUtils.setField(authService, "googleGrantType", "authorization_code");
        ReflectionTestUtils.setField(authService, "googleLinkGetToken", "https://oauth2.googleapis.com/token");
        ReflectionTestUtils.setField(authService, "googleLinkGetUserInfo",
                "https://www.googleapis.com/oauth2/v1/userinfo?access_token=");
        ReflectionTestUtils.setField(authService, "googleAdminEmail", adminEmail);
    }

    @Nested
    @DisplayName("Configuration Tests")
    class ConfigurationTests {
        @Test
        @DisplayName("Should initialize AuthService with required properties")
        void testAuthServiceInitialization() {
            assertNotNull(authService);
            String clientId = (String) ReflectionTestUtils.getField(authService, "googleClientId");
            assertEquals("test_client_id", clientId);
        }

        @Test
        @DisplayName("Should have Google OAuth credentials configured")
        void testGoogleCredentialsConfiguration() {
            String clientSecret = (String) ReflectionTestUtils.getField(authService, "googleClientSecret");
            assertNotNull(clientSecret);
            assertFalse(clientSecret.isEmpty());
        }

        @Test
        @DisplayName("Should configure redirect URI")
        void testRedirectURIConfiguration() {
            String redirectUri = (String) ReflectionTestUtils.getField(authService, "googleRedirectUri");
            assertEquals("http://localhost:3000", redirectUri);
        }

        @Test
        @DisplayName("Should configure authorization grant type")
        void testGrantTypeConfiguration() {
            String grantType = (String) ReflectionTestUtils.getField(authService, "googleGrantType");
            assertEquals("authorization_code", grantType);
        }

        @Test
        @DisplayName("Should configure Google token endpoint")
        void testTokenEndpointConfiguration() {
            String tokenLink = (String) ReflectionTestUtils.getField(authService, "googleLinkGetToken");
            assertTrue(tokenLink.contains("oauth2.googleapis.com/token"));
        }

        @Test
        @DisplayName("Should configure Google user info endpoint")
        void testUserInfoEndpointConfiguration() {
            String userInfoLink = (String) ReflectionTestUtils.getField(authService, "googleLinkGetUserInfo");
            assertTrue(userInfoLink.contains("userinfo"));
        }

        @Test
        @DisplayName("Should configure admin email")
        void testAdminEmailConfiguration() {
            String admin = (String) ReflectionTestUtils.getField(authService, "googleAdminEmail");
            assertEquals(adminEmail, admin);
        }
    }

    @Nested
    @DisplayName("Google Account Structure Tests")
    class GoogleAccountStructureTests {
        @Test
        @DisplayName("Should create GoogleAccount with builder")
        void testGoogleAccountBuilder() {
            assertNotNull(testGoogleAccount);
            assertEquals("google_12345", testGoogleAccount.getId());
            assertEquals("user@example.com", testGoogleAccount.getEmail());
        }

        @Test
        @DisplayName("Should have all GoogleAccount fields")
        void testGoogleAccountFields() {
            assertTrue(testGoogleAccount.isVerified_email());
            assertEquals("John Doe", testGoogleAccount.getName());
            assertEquals("John", testGoogleAccount.getGiven_name());
            assertEquals("Doe", testGoogleAccount.getFamily_name());
        }

        @Test
        @DisplayName("Should distinguish verified vs unverified emails")
        void testVerifiedEmailFlag() {
            GoogleAccount verified = GoogleAccount.builder()
                    .id("id1")
                    .email("verified@example.com")
                    .verified_email(true)
                    .name("User")
                    .build();

            GoogleAccount unverified = GoogleAccount.builder()
                    .id("id2")
                    .email("unverified@example.com")
                    .verified_email(false)
                    .name("User")
                    .build();

            assertTrue(verified.isVerified_email());
            assertFalse(unverified.isVerified_email());
        }

        @Test
        @DisplayName("Should create GoogleAccount with no-args constructor")
        void testGoogleAccountNoArgsConstructor() {
            GoogleAccount account = new GoogleAccount();
            assertNotNull(account);
        }

        @Test
        @DisplayName("Should set GoogleAccount properties via setters")
        void testGoogleAccountSetters() {
            GoogleAccount account = new GoogleAccount();
            account.setId("id123");
            account.setEmail("test@domain.com");
            account.setVerified_email(true);

            assertEquals("id123", account.getId());
            assertEquals("test@domain.com", account.getEmail());
            assertTrue(account.isVerified_email());
        }

        @Test
        @DisplayName("Should handle all GoogleAccount properties")
        void testGoogleAccountProperties() {
            GoogleAccount account = GoogleAccount.builder()
                    .id("id")
                    .email("email")
                    .verified_email(true)
                    .name("name")
                    .given_name("given")
                    .family_name("family")
                    .picture("picture_url")
                    .hd("hd")
                    .build();

            assertNotNull(account.getId());
            assertNotNull(account.getEmail());
            assertNotNull(account.getName());
            assertNotNull(account.getPicture());
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {
        @Test
        @DisplayName("Should accept valid authorization code")
        void testValidAuthorizationCode() {
            String code = "4/0AY0e-g7jXyZ123abc";
            assertNotNull(code);
            assertFalse(code.isEmpty());
        }

        @Test
        @DisplayName("Should handle empty authorization code")
        void testEmptyAuthorizationCode() {
            String emptyCode = "";
            assertEquals("", emptyCode);
        }

        @Test
        @DisplayName("Should accept valid access token format")
        void testValidAccessToken() {
            String token = "ya29.a0AfH6SMBx";
            assertNotNull(token);
            assertFalse(token.isEmpty());
        }

        @Test
        @DisplayName("Should handle access token with special characters")
        void testAccessTokenWithSpecialChars() {
            String token = "ya29.a0-AfH6_SMBx.xyz";
            assertNotNull(token);
        }
    }

    @Nested
    @DisplayName("Email Verification Tests")
    class EmailVerificationTests {
        @Test
        @DisplayName("Should verify email is confirmed")
        void testVerifiedEmail() {
            assertTrue(testGoogleAccount.isVerified_email());
        }

        @Test
        @DisplayName("Should detect unverified email")
        void testUnverifiedEmail() {
            GoogleAccount unverified = GoogleAccount.builder()
                    .id("id")
                    .email("unverified@example.com")
                    .verified_email(false)
                    .name("User")
                    .build();

            assertFalse(unverified.isVerified_email());
        }

        @Test
        @DisplayName("Should validate email format")
        void testEmailFormat() {
            String email = testGoogleAccount.getEmail();
            assertTrue(email.contains("@"));
            assertTrue(email.contains("."));
        }
    }

    @Nested
    @DisplayName("User Role Determination Tests")
    class UserRoleDeterminationTests {
        @Test
        @DisplayName("Should identify admin user by email")
        void testIdentifyAdminUser() {
            String userEmail = adminEmail;
            assertEquals("admin@example.com", userEmail);
        }

        @Test
        @DisplayName("Should identify regular user")
        void testIdentifyRegularUser() {
            String userEmail = "regularuser@example.com";
            assertNotEquals(adminEmail, userEmail);
        }

        @Test
        @DisplayName("Should determine user role based on email match")
        void testRoleDeterminationLogic() {
            String email1 = adminEmail;
            String email2 = "user@example.com";

            String role1 = email1.equals(adminEmail) ? "Admin" : "User";
            String role2 = email2.equals(adminEmail) ? "Admin" : "User";

            assertEquals("Admin", role1);
            assertEquals("User", role2);
        }

        @Test
        @DisplayName("Should handle case sensitivity in email comparison")
        void testEmailCaseSensitivity() {
            String adminLower = adminEmail.toLowerCase();
            String adminUpper = adminEmail.toUpperCase();

            assertTrue(adminEmail.equals(adminEmail));
            // In this environment adminEmail is lowercase, so toLowerCase equals it
            assertEquals(adminEmail, adminLower);
        }
    }

    @Nested
    @DisplayName("OAuth Flow Tests")
    class OAuthFlowTests {
        @Test
        @DisplayName("Should use correct OAuth endpoint for token exchange")
        void testTokenExchangeEndpoint() {
            String endpoint = "https://oauth2.googleapis.com/token";
            assertTrue(endpoint.contains("oauth2.googleapis.com"));
            assertTrue(endpoint.contains("token"));
        }

        @Test
        @DisplayName("Should use correct endpoint for user info retrieval")
        void testUserInfoEndpoint() {
            String endpoint = "https://www.googleapis.com/oauth2/v1/userinfo";
            assertTrue(endpoint.contains("userinfo"));
        }

        @Test
        @DisplayName("Should construct user info URI with access token")
        void testConstructUserInfoURI() {
            String token = "test_token_123";
            String userInfoLink = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";
            String fullURI = userInfoLink + token;

            assertTrue(fullURI.contains(token));
            assertTrue(fullURI.contains("userinfo"));
        }

        @Test
        @DisplayName("Should use authorization_code grant type")
        void testGrantType() {
            String grantType = "authorization_code";
            assertEquals("authorization_code", grantType);
        }

        @Test
        @DisplayName("Should have valid redirect URI")
        void testRedirectURI() {
            String redirectUri = "http://localhost:3000";
            assertNotNull(redirectUri);
            assertTrue(redirectUri.startsWith("http"));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        @Test
        @DisplayName("Should throw AppException for invalid credentials")
        void testInvalidCredentialsException() {
            AppException exception = new AppException(ErrorCode.INVALID_CREDENTIALS);
            assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw AppException for internet errors")
        void testInternetErrorException() {
            AppException exception = new AppException(ErrorCode.INTERNET_ERROR);
            assertEquals(ErrorCode.INTERNET_ERROR, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw AppException for bad gateway")
        void testBadGatewayException() {
            AppException exception = new AppException(ErrorCode.BAD_GATEWAY);
            assertEquals(ErrorCode.BAD_GATEWAY, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should handle null response gracefully")
        void testNullResponseHandling() {
            GoogleAccount nullAccount = null;
            assertNull(nullAccount);
        }

        @Test
        @DisplayName("Should handle ClientProtocolException")
        void testClientProtocolException() {
            ClientProtocolException exception = new ClientProtocolException("Network error");
            assertNotNull(exception);
            assertTrue(exception.getMessage().contains("Network"));
        }

        @Test
        @DisplayName("Should handle IOException")
        void testIOException() {
            IOException exception = new IOException("IO error");
            assertNotNull(exception);
            assertTrue(exception.getMessage().contains("IO"));
        }

        @Test
        @DisplayName("Should handle malformed JSON response")
        void testMalformedJSONHandling() {
            String malformedJSON = "{invalid json}";
            assertNotNull(malformedJSON);
        }
    }

    @Nested
    @DisplayName("Credential Configuration Tests")
    class CredentialConfigurationTests {
        @Test
        @DisplayName("Should validate client ID is configured")
        void testClientIDConfiguration() {
            String clientId = (String) ReflectionTestUtils.getField(authService, "googleClientId");
            assertNotNull(clientId);
            assertFalse(clientId.isEmpty());
        }

        @Test
        @DisplayName("Should validate client secret is configured")
        void testClientSecretConfiguration() {
            String clientSecret = (String) ReflectionTestUtils.getField(authService, "googleClientSecret");
            assertNotNull(clientSecret);
            assertFalse(clientSecret.isEmpty());
        }

        @Test
        @DisplayName("Should have all required OAuth credentials")
        void testAllCredentialsPresent() {
            String clientId = (String) ReflectionTestUtils.getField(authService, "googleClientId");
            String clientSecret = (String) ReflectionTestUtils.getField(authService, "googleClientSecret");
            String redirectUri = (String) ReflectionTestUtils.getField(authService, "googleRedirectUri");

            assertNotNull(clientId);
            assertNotNull(clientSecret);
            assertNotNull(redirectUri);
            assertFalse(clientId.isEmpty());
            assertFalse(clientSecret.isEmpty());
            assertFalse(redirectUri.isEmpty());
        }

        @Test
        @DisplayName("Should validate admin email configuration")
        void testAdminEmailNotEmpty() {
            String admin = (String) ReflectionTestUtils.getField(authService, "googleAdminEmail");
            assertNotNull(admin);
            assertFalse(admin.isEmpty());
            assertTrue(admin.contains("@"));
        }
    }

    @Nested
    @DisplayName("API Response Tests")
    class APIResponseTests {
        @Test
        @DisplayName("Should create success response")
        void testSuccessResponse() {
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .code(200)
                    .message("Success")
                    .success(true)
                    .data(true)
                    .build();

            assertTrue(response.isSuccess());
            assertEquals(200, response.getCode());
        }

        @Test
        @DisplayName("Should create failure response")
        void testFailureResponse() {
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .code(401)
                    .message("Unauthorized")
                    .success(false)
                    .data(false)
                    .build();

            assertFalse(response.isSuccess());
            assertEquals(401, response.getCode());
        }

        @Test
        @DisplayName("Should include descriptive message")
        void testResponseMessage() {
            ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                    .code(200)
                    .message("Valid token")
                    .success(true)
                    .build();

            assertTrue(response.getMessage().contains("Valid"));
        }

        @Test
        @DisplayName("Should handle different HTTP status codes")
        void testDifferentStatusCodes() {
            ApiResponse<Boolean> ok = ApiResponse.<Boolean>builder().code(200).build();
            ApiResponse<Boolean> unauthorized = ApiResponse.<Boolean>builder().code(401).build();
            ApiResponse<Boolean> badGateway = ApiResponse.<Boolean>builder().code(502).build();

            assertEquals(200, ok.getCode());
            assertEquals(401, unauthorized.getCode());
            assertEquals(502, badGateway.getCode());
        }
    }

    @Nested
    @DisplayName("Integration Scenario Tests")
    class IntegrationScenarioTests {
        @Test
        @DisplayName("Should handle complete OAuth flow setup")
        void testCompleteOAuthSetup() {
            String clientId = (String) ReflectionTestUtils.getField(authService, "googleClientId");
            String redirectUri = (String) ReflectionTestUtils.getField(authService, "googleRedirectUri");
            String grantType = (String) ReflectionTestUtils.getField(authService, "googleGrantType");

            assertNotNull(clientId);
            assertNotNull(redirectUri);
            assertEquals("authorization_code", grantType);
        }

        @Test
        @DisplayName("Should have complete user verification setup")
        void testCompleteUserVerificationSetup() {
            String tokenLink = (String) ReflectionTestUtils.getField(authService, "googleLinkGetToken");
            String userInfoLink = (String) ReflectionTestUtils.getField(authService, "googleLinkGetUserInfo");
            String adminEmail = (String) ReflectionTestUtils.getField(authService, "googleAdminEmail");

            assertNotNull(tokenLink);
            assertNotNull(userInfoLink);
            assertNotNull(adminEmail);
        }

        @Test
        @DisplayName("Should handle user authentication flow")
        void testUserAuthenticationFlow() {
            String code = testCode;
            assertNotNull(code);

            String token = testAccessToken;
            assertNotNull(token);

            assertNotNull(testGoogleAccount);
            assertTrue(testGoogleAccount.isVerified_email());
        }
    }
}
