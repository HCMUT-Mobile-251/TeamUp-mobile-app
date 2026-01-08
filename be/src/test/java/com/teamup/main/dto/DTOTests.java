package com.teamup.main.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.dto.response.AuthResponse;
import com.teamup.main.dto.response.UserResponse;
import com.teamup.main.dto.response.GroupResponse;
import com.teamup.main.dto.response.NotificationResponse;
import com.teamup.main.dto.request.GoogleAccount;
import com.teamup.main.dto.request.UserUpdateRequest;
import com.teamup.main.model.Users;

@DisplayName("DTO Tests - Phase 6")
class DTOTests {

    @Nested
    @DisplayName("ApiResponse<T> Tests")
    class ApiResponseTests {
        private ApiResponse<String> apiResponse;

        @BeforeEach
        void setUp() {
            apiResponse = new ApiResponse<>();
        }

        @Test
        @DisplayName("Should create empty ApiResponse")
        void testCreateEmptyApiResponse() {
            assertNotNull(apiResponse);
        }

        @Test
        @DisplayName("Should set and get success status")
        void testSetGetSuccess() {
            apiResponse.setSuccess(true);
            assertTrue(apiResponse.isSuccess());
        }

        @Test
        @DisplayName("Should set and get message")
        void testSetGetMessage() {
            apiResponse.setMessage("Operation successful");
            assertEquals("Operation successful", apiResponse.getMessage());
        }

        @Test
        @DisplayName("Should set and get data")
        void testSetGetData() {
            apiResponse.setData("test data");
            assertEquals("test data", apiResponse.getData());
        }

        @Test
        @DisplayName("Should handle null data")
        void testNullData() {
            apiResponse.setData(null);
            assertNull(apiResponse.getData());
        }

        @Test
        @DisplayName("Should handle failure response")
        void testFailureResponse() {
            ApiResponse<String> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("Error occurred");

            assertFalse(response.isSuccess());
            assertEquals("Error occurred", response.getMessage());
        }
    }

    @Nested
    @DisplayName("AuthResponse Tests")
    class AuthResponseTests {
        private AuthResponse authResponse;
        private Users testUser;

        @BeforeEach
        void setUp() {
            authResponse = AuthResponse.builder().build();
            testUser = new Users();
            testUser.setUserId("USR001");
            testUser.setEmail("test@example.com");
        }

        @Test
        @DisplayName("Should create AuthResponse")
        void testCreateAuthResponse() {
            assertNotNull(authResponse);
        }

        @Test
        @DisplayName("Should set and get accessToken")
        void testSetGetAccessToken() {
            authResponse = AuthResponse.builder()
                    .accessToken("token123")
                    .build();
            assertEquals("token123", authResponse.getAccessToken());
        }

        @Test
        @DisplayName("Should set and get user")
        void testSetGetUser() {
            authResponse = AuthResponse.builder()
                    .accessToken("token")
                    .user(testUser)
                    .build();
            
            assertNotNull(authResponse.getUser());
            assertEquals("USR001", authResponse.getUser().getUserId());
        }

        @Test
        @DisplayName("Should build complete AuthResponse")
        void testCompleteBuilder() {
            AuthResponse response = AuthResponse.builder()
                    .accessToken("jwt_token_123")
                    .user(testUser)
                    .build();

            assertEquals("jwt_token_123", response.getAccessToken());
            assertEquals("USR001", response.getUser().getUserId());
            assertEquals("test@example.com", response.getUser().getEmail());
        }
    }

    @Nested
    @DisplayName("UserResponse Tests")
    class UserResponseTests {
        private UserResponse userResponse;

        @BeforeEach
        void setUp() {
            userResponse = new UserResponse();
        }

        @Test
        @DisplayName("Should create UserResponse")
        void testCreateUserResponse() {
            assertNotNull(userResponse);
        }

        @Test
        @DisplayName("Should set and get userId")
        void testSetGetUserId() {
            userResponse.setUserId("USR001");
            assertEquals("USR001", userResponse.getUserId());
        }

        @Test
        @DisplayName("Should set and get email")
        void testSetGetEmail() {
            userResponse.setEmail("john@example.com");
            assertEquals("john@example.com", userResponse.getEmail());
        }

        @Test
        @DisplayName("Should set and get fullName")
        void testSetGetFullName() {
            userResponse.setFullName("John Doe");
            assertEquals("John Doe", userResponse.getFullName());
        }

        @Test
        @DisplayName("Should set and get studentId")
        void testSetGetStudentId() {
            userResponse.setStudentId("20205001");
            assertEquals("20205001", userResponse.getStudentId());
        }

        @Test
        @DisplayName("Should set and get phoneNumber")
        void testSetGetPhoneNumber() {
            userResponse.setPhoneNumber("0123456789");
            assertEquals("0123456789", userResponse.getPhoneNumber());
        }

        @Test
        @DisplayName("Should set and get faculty")
        void testSetGetFaculty() {
            userResponse.setFaculty("Computer Science");
            assertEquals("Computer Science", userResponse.getFaculty());
        }

        @Test
        @DisplayName("Should handle all fields")
        void testAllFields() {
            userResponse.setUserId("U1");
            userResponse.setStudentId("S1");
            userResponse.setFirstName("John");
            userResponse.setLastName("Doe");
            userResponse.setFullName("John Doe");
            userResponse.setEmail("john@test.com");
            userResponse.setPhoneNumber("123456");
            userResponse.setFaculty("CSE");

            assertEquals("U1", userResponse.getUserId());
            assertEquals("S1", userResponse.getStudentId());
            assertEquals("John", userResponse.getFirstName());
            assertEquals("Doe", userResponse.getLastName());
            assertEquals("John Doe", userResponse.getFullName());
            assertEquals("john@test.com", userResponse.getEmail());
            assertEquals("123456", userResponse.getPhoneNumber());
            assertEquals("CSE", userResponse.getFaculty());
        }
    }

    @Nested
    @DisplayName("GroupResponse Tests")
    class GroupResponseTests {
        private GroupResponse groupResponse;

        @BeforeEach
        void setUp() {
            groupResponse = new GroupResponse();
        }

        @Test
        @DisplayName("Should create GroupResponse")
        void testCreateGroupResponse() {
            assertNotNull(groupResponse);
        }

        @Test
        @DisplayName("Should set and get groupId")
        void testSetGetGroupId() {
            groupResponse.setGroupId("G001");
            assertEquals("G001", groupResponse.getGroupId());
        }

        @Test
        @DisplayName("Should set and get name")
        void testSetGetName() {
            groupResponse.setName("Java Study Group");
            assertEquals("Java Study Group", groupResponse.getName());
        }

        @Test
        @DisplayName("Should set and get semester")
        void testSetGetSemester() {
            groupResponse.setSemester(1);
            assertEquals(1, groupResponse.getSemester());
        }

        @Test
        @DisplayName("Should set and get isMember")
        void testSetGetIsMember() {
            groupResponse.setIsMember(true);
            assertTrue(groupResponse.isIsMember());
        }

        @Test
        @DisplayName("Should handle all group fields")
        void testAllGroupFields() {
            groupResponse.setGroupId("G001");
            groupResponse.setName("Data Structures");
            groupResponse.setTopicName("DS Advanced");
            groupResponse.setGroupClass("Class A");
            groupResponse.setSemester(2);
            groupResponse.setIsMember(false);

            assertEquals("G001", groupResponse.getGroupId());
            assertEquals("Data Structures", groupResponse.getName());
            assertEquals("DS Advanced", groupResponse.getTopicName());
            assertEquals("Class A", groupResponse.getGroupClass());
            assertEquals(2, groupResponse.getSemester());
            assertFalse(groupResponse.isIsMember());
        }
    }

    @Nested
    @DisplayName("NotificationResponse Tests")
    class NotificationResponseTests {
        private NotificationResponse notificationResponse;

        @BeforeEach
        void setUp() {
            notificationResponse = new NotificationResponse();
        }

        @Test
        @DisplayName("Should create NotificationResponse")
        void testCreateNotificationResponse() {
            assertNotNull(notificationResponse);
        }

        @Test
        @DisplayName("Should set and get notificationId")
        void testSetGetNotificationId() {
            notificationResponse.setNotificationId("N001");
            assertEquals("N001", notificationResponse.getNotificationId());
        }

        @Test
        @DisplayName("Should set and get message")
        void testSetGetMessage() {
            notificationResponse.setMessage("You have a new notification");
            assertEquals("You have a new notification", notificationResponse.getMessage());
        }
    }

    @Nested
    @DisplayName("GoogleAccount DTO Tests")
    class GoogleAccountDTOTests {
        private GoogleAccount googleAccount;

        @BeforeEach
        void setUp() {
            googleAccount = new GoogleAccount();
        }

        @Test
        @DisplayName("Should create GoogleAccount")
        void testCreateGoogleAccount() {
            assertNotNull(googleAccount);
        }

        @Test
        @DisplayName("Should set and get id")
        void testSetGetId() {
            googleAccount.setId("google_id_123");
            assertEquals("google_id_123", googleAccount.getId());
        }

        @Test
        @DisplayName("Should set and get email")
        void testSetGetEmail() {
            googleAccount.setEmail("user@gmail.com");
            assertEquals("user@gmail.com", googleAccount.getEmail());
        }

        @Test
        @DisplayName("Should set and get verified_email")
        void testSetGetVerifiedEmail() {
            googleAccount.setVerified_email(true);
            assertTrue(googleAccount.isVerified_email());
        }

        @Test
        @DisplayName("Should set and get name")
        void testSetGetName() {
            googleAccount.setName("John Doe");
            assertEquals("John Doe", googleAccount.getName());
        }

        @Test
        @DisplayName("Should set and get given_name")
        void testSetGetGivenName() {
            googleAccount.setGiven_name("John");
            assertEquals("John", googleAccount.getGiven_name());
        }

        @Test
        @DisplayName("Should set and get family_name")
        void testSetGetFamilyName() {
            googleAccount.setFamily_name("Doe");
            assertEquals("Doe", googleAccount.getFamily_name());
        }

        @Test
        @DisplayName("Should set and get picture")
        void testSetGetPicture() {
            googleAccount.setPicture("https://example.com/pic.jpg");
            assertEquals("https://example.com/pic.jpg", googleAccount.getPicture());
        }

        @Test
        @DisplayName("Should build GoogleAccount with builder")
        void testBuilderPattern() {
            GoogleAccount account = GoogleAccount.builder()
                    .id("g123")
                    .email("test@gmail.com")
                    .name("Test User")
                    .given_name("Test")
                    .family_name("User")
                    .verified_email(true)
                    .build();

            assertEquals("g123", account.getId());
            assertEquals("test@gmail.com", account.getEmail());
            assertEquals("Test User", account.getName());
            assertTrue(account.isVerified_email());
        }
    }

    @Nested
    @DisplayName("UserUpdateRequest DTO Tests")
    class UserUpdateRequestDTOTests {
        private UserUpdateRequest updateRequest;

        @BeforeEach
        void setUp() {
            updateRequest = new UserUpdateRequest();
        }

        @Test
        @DisplayName("Should create UserUpdateRequest")
        void testCreateUpdateRequest() {
            assertNotNull(updateRequest);
        }

        @Test
        @DisplayName("Should set and get email")
        void testSetGetEmail() {
            updateRequest.setEmail("newemail@example.com");
            assertEquals("newemail@example.com", updateRequest.getEmail());
        }

        @Test
        @DisplayName("Should set and get firstName")
        void testSetGetFirstName() {
            updateRequest.setFirstName("Jane");
            assertEquals("Jane", updateRequest.getFirstName());
        }

        @Test
        @DisplayName("Should set and get lastName")
        void testSetGetLastName() {
            updateRequest.setLastName("Smith");
            assertEquals("Smith", updateRequest.getLastName());
        }

        @Test
        @DisplayName("Should set and get phoneNumber")
        void testSetGetPhoneNumber() {
            updateRequest.setPhoneNumber("0987654321");
            assertEquals("0987654321", updateRequest.getPhoneNumber());
        }

        @Test
        @DisplayName("Should handle partial updates")
        void testPartialUpdate() {
            updateRequest.setEmail("new@example.com");
            updateRequest.setFirstName("Jane");
            
            assertEquals("new@example.com", updateRequest.getEmail());
            assertEquals("Jane", updateRequest.getFirstName());
            assertNull(updateRequest.getLastName());
            assertNull(updateRequest.getPhoneNumber());
        }
    }
}
