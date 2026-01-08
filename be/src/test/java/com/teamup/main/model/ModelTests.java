package com.teamup.main.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Model Entity Tests - Phase 6")
class ModelTests {

    @Nested
    @DisplayName("Users Model Tests")
    class UsersModelTests {
        private Users user;

        @BeforeEach
        void setUp() {
            user = new Users();
        }

        @Test
        @DisplayName("Should create Users entity")
        void testCreateUser() {
            assertNotNull(user);
        }

        @Test
        @DisplayName("Should set and get userId")
        void testSetGetUserId() {
            user.setUserId("USR001");
            assertEquals("USR001", user.getUserId());
        }

        @Test
        @DisplayName("Should set and get studentId")
        void testSetGetStudentId() {
            user.setStudentId("20205001");
            assertEquals("20205001", user.getStudentId());
        }

        @Test
        @DisplayName("Should set and get firstName")
        void testSetGetFirstName() {
            user.setFirstName("John");
            assertEquals("John", user.getFirstName());
        }

        @Test
        @DisplayName("Should set and get lastName")
        void testSetGetLastName() {
            user.setLastName("Doe");
            assertEquals("Doe", user.getLastName());
        }

        @Test
        @DisplayName("Should set and get fullName")
        void testSetGetFullName() {
            user.setFullName("John Doe");
            assertEquals("John Doe", user.getFullName());
        }

        @Test
        @DisplayName("Should set and get email")
        void testSetGetEmail() {
            user.setEmail("john@hcmut.edu.vn");
            assertEquals("john@hcmut.edu.vn", user.getEmail());
        }

        @Test
        @DisplayName("Should set and get phoneNumber")
        void testSetGetPhoneNumber() {
            user.setPhoneNumber("0123456789");
            assertEquals("0123456789", user.getPhoneNumber());
        }

        @Test
        @DisplayName("Should set and get faculty")
        void testSetGetFaculty() {
            user.setFaculty("Computer Science");
            assertEquals("Computer Science", user.getFaculty());
        }

        @Test
        @DisplayName("Should initialize groups as HashSet")
        void testGroupsInitialization() {
            assertNotNull(user.getGroups());
            assertTrue(user.getGroups() instanceof HashSet);
        }

        @Test
        @DisplayName("Should initialize userTags as HashSet")
        void testUserTagsInitialization() {
            assertNotNull(user.getUserTags());
            assertTrue(user.getUserTags() instanceof HashSet);
        }

        @Test
        @DisplayName("Should add group to groups set")
        void testAddGroup() {
            GroupMember groupMember = new GroupMember();
            user.getGroups().add(groupMember);
            assertEquals(1, user.getGroups().size());
        }

        @Test
        @DisplayName("Should add userTag to userTags set")
        void testAddUserTag() {
            UserTag userTag = new UserTag();
            user.getUserTags().add(userTag);
            assertEquals(1, user.getUserTags().size());
        }

        @Test
        @DisplayName("Should handle multiple values")
        void testMultipleValues() {
            user.setUserId("U1");
            user.setStudentId("S1");
            user.setFirstName("Alice");
            user.setLastName("Smith");
            user.setFullName("Alice Smith");
            user.setEmail("alice@test.com");
            user.setPhoneNumber("9876543210");
            user.setFaculty("Engineering");

            assertEquals("U1", user.getUserId());
            assertEquals("S1", user.getStudentId());
            assertEquals("Alice", user.getFirstName());
            assertEquals("Smith", user.getLastName());
            assertEquals("Alice Smith", user.getFullName());
            assertEquals("alice@test.com", user.getEmail());
            assertEquals("9876543210", user.getPhoneNumber());
            assertEquals("Engineering", user.getFaculty());
        }
    }

    @Nested
    @DisplayName("Groups Model Tests")
    class GroupsModelTests {
        private Groups group;

        @BeforeEach
        void setUp() {
            group = new Groups();
        }

        @Test
        @DisplayName("Should create Groups entity")
        void testCreateGroup() {
            assertNotNull(group);
        }

        @Test
        @DisplayName("Should set and get groupId")
        void testSetGetGroupId() {
            group.setGroupId("G001");
            assertEquals("G001", group.getGroupId());
        }

        @Test
        @DisplayName("Should set and get name")
        void testSetGetName() {
            group.setName("Java Study Group");
            assertEquals("Java Study Group", group.getName());
        }

        @Test
        @DisplayName("Should set and get topicName")
        void testSetGetTopicName() {
            group.setTopicName("Advanced Java");
            assertEquals("Advanced Java", group.getTopicName());
        }

        @Test
        @DisplayName("Should set and get groupClass")
        void testSetGetGroupClass() {
            group.setGroupClass("Class A");
            assertEquals("Class A", group.getGroupClass());
        }

        @Test
        @DisplayName("Should set and get semester")
        void testSetGetSemester() {
            group.setSemester(1);
            assertEquals(1, group.getSemester());
        }

        @Test
        @DisplayName("Should set and get course")
        void testSetGetCourse() {
            Courses course = new Courses();
            course.setCourseId("CS101");
            group.setCourse(course);
            assertEquals("CS101", group.getCourse().getCourseId());
        }

        @Test
        @DisplayName("Should initialize groupMembers")
        void testGroupMembersInitialization() {
            assertNotNull(group.getGroupMembers());
        }

        @Test
        @DisplayName("Should initialize groupTags")
        void testGroupTagsInitialization() {
            assertNotNull(group.getGroupTags());
        }

        @Test
        @DisplayName("Should handle multiple groups")
        void testMultipleGroupsProperties() {
            Groups group2 = new Groups();
            group.setGroupId("G1");
            group.setName("Group 1");
            group2.setGroupId("G2");
            group2.setName("Group 2");

            assertEquals("G1", group.getGroupId());
            assertEquals("G2", group2.getGroupId());
            assertEquals("Group 1", group.getName());
            assertEquals("Group 2", group2.getName());
        }
    }

    @Nested
    @DisplayName("Courses Model Tests")
    class CoursesModelTests {
        private Courses course;

        @BeforeEach
        void setUp() {
            course = new Courses();
        }

        @Test
        @DisplayName("Should create Courses entity")
        void testCreateCourse() {
            assertNotNull(course);
        }

        @Test
        @DisplayName("Should set and get courseId")
        void testSetGetCourseId() {
            course.setCourseId("CS101");
            assertEquals("CS101", course.getCourseId());
        }

        @Test
        @DisplayName("Should set and get name")
        void testSetGetName() {
            course.setName("Data Structures");
            assertEquals("Data Structures", course.getName());
        }

        @Test
        @DisplayName("Should set and get credits")
        void testSetGetCredits() {
            course.setCredits(3);
            assertEquals(3, course.getCredits());
        }

        @Test
        @DisplayName("Should handle multiple courses")
        void testMultipleCourses() {
            Courses course2 = new Courses();
            course.setCourseId("CS101");
            course.setName("Intro to CS");
            course.setCredits(3);

            course2.setCourseId("CS102");
            course2.setName("OOP");
            course2.setCredits(4);

            assertEquals("CS101", course.getCourseId());
            assertEquals("CS102", course2.getCourseId());
            assertEquals(3, course.getCredits());
            assertEquals(4, course2.getCredits());
        }
    }

    @Nested
    @DisplayName("Tags Model Tests")
    class TagsModelTests {
        private Tags tag;

        @BeforeEach
        void setUp() {
            tag = new Tags();
        }

        @Test
        @DisplayName("Should create Tags entity")
        void testCreateTag() {
            assertNotNull(tag);
        }

        @Test
        @DisplayName("Should set and get tagId")
        void testSetGetTagId() {
            tag.setTagId("TAG001");
            assertEquals("TAG001", tag.getTagId());
        }

        @Test
        @DisplayName("Should set and get tagName")
        void testSetGetTagName() {
            tag.setTagName("Java");
            assertEquals("Java", tag.getTagName());
        }

        @Test
        @DisplayName("Should handle multiple tags")
        void testMultipleTags() {
            Tags tag2 = new Tags();
            tag.setTagId("T1");
            tag.setTagName("Python");

            tag2.setTagId("T2");
            tag2.setTagName("JavaScript");

            assertEquals("T1", tag.getTagId());
            assertEquals("T2", tag2.getTagId());
            assertEquals("Python", tag.getTagName());
            assertEquals("JavaScript", tag2.getTagName());
        }
    }

    @Nested
    @DisplayName("GroupMember Model Tests")
    class GroupMemberModelTests {
        private GroupMember groupMember;

        @BeforeEach
        void setUp() {
            groupMember = new GroupMember();
        }

        @Test
        @DisplayName("Should create GroupMember entity")
        void testCreateGroupMember() {
            assertNotNull(groupMember);
        }

        @Test
        @DisplayName("Should set and get user")
        void testSetGetUser() {
            Users user = new Users();
            user.setUserId("USR001");
            groupMember.setUser(user);
            assertEquals("USR001", groupMember.getUser().getUserId());
        }

        @Test
        @DisplayName("Should set and get group")
        void testSetGetGroup() {
            Groups group = new Groups();
            group.setGroupId("G001");
            groupMember.setGroup(group);
            assertEquals("G001", groupMember.getGroup().getGroupId());
        }

        @Test
        @DisplayName("Should set and get status")
        void testSetGetStatus() {
            groupMember.setStatus(com.teamup.main.enums.GroupStatus.JOINED);
            assertEquals(com.teamup.main.enums.GroupStatus.JOINED, groupMember.getStatus());
        }

        @Test
        @DisplayName("Should set and get joinMessage")
        void testSetGetJoinMessage() {
            groupMember.setJoinMessage("I want to join");
            assertEquals("I want to join", groupMember.getJoinMessage());
        }

        @Test
        @DisplayName("Should handle multiple group members")
        void testMultipleGroupMembers() {
            GroupMember member2 = new GroupMember();
            Users user1 = new Users();
            Users user2 = new Users();
            user1.setUserId("U1");
            user2.setUserId("U2");

            groupMember.setUser(user1);
            member2.setUser(user2);

            assertEquals("U1", groupMember.getUser().getUserId());
            assertEquals("U2", member2.getUser().getUserId());
        }
    }

    @Nested
    @DisplayName("UserTag Model Tests")
    class UserTagModelTests {
        private UserTag userTag;

        @BeforeEach
        void setUp() {
            userTag = new UserTag();
        }

        @Test
        @DisplayName("Should create UserTag entity")
        void testCreateUserTag() {
            assertNotNull(userTag);
        }

        @Test
        @DisplayName("Should set and get user")
        void testSetGetUser() {
            Users user = new Users();
            user.setUserId("USR001");
            userTag.setUser(user);
            assertEquals("USR001", userTag.getUser().getUserId());
        }

        @Test
        @DisplayName("Should set and get tag")
        void testSetGetTag() {
            Tags tag = new Tags();
            tag.setTagId("TAG001");
            userTag.setTag(tag);
            assertEquals("TAG001", userTag.getTag().getTagId());
        }

        @Test
        @DisplayName("Should handle multiple userTags")
        void testMultipleUserTags() {
            UserTag userTag2 = new UserTag();
            Tags tag1 = new Tags();
            Tags tag2 = new Tags();
            tag1.setTagId("T1");
            tag2.setTagId("T2");

            userTag.setTag(tag1);
            userTag2.setTag(tag2);

            assertEquals("T1", userTag.getTag().getTagId());
            assertEquals("T2", userTag2.getTag().getTagId());
        }
    }

    @Nested
    @DisplayName("GroupTag Model Tests")
    class GroupTagModelTests {
        private GroupTag groupTag;

        @BeforeEach
        void setUp() {
            groupTag = new GroupTag();
        }

        @Test
        @DisplayName("Should create GroupTag entity")
        void testCreateGroupTag() {
            assertNotNull(groupTag);
        }

        @Test
        @DisplayName("Should set and get group")
        void testSetGetGroup() {
            Groups group = new Groups();
            group.setGroupId("G001");
            groupTag.setGroup(group);
            assertEquals("G001", groupTag.getGroup().getGroupId());
        }

        @Test
        @DisplayName("Should set and get tag")
        void testSetGetTag() {
            Tags tag = new Tags();
            tag.setTagId("TAG001");
            groupTag.setTag(tag);
            assertEquals("TAG001", groupTag.getTag().getTagId());
        }

        @Test
        @DisplayName("Should handle multiple groupTags")
        void testMultipleGroupTags() {
            GroupTag groupTag2 = new GroupTag();
            Groups group1 = new Groups();
            Groups group2 = new Groups();
            group1.setGroupId("G1");
            group2.setGroupId("G2");

            groupTag.setGroup(group1);
            groupTag2.setGroup(group2);

            assertEquals("G1", groupTag.getGroup().getGroupId());
            assertEquals("G2", groupTag2.getGroup().getGroupId());
        }
    }

    @Nested
    @DisplayName("PairId Model Tests")
    class PairIdModelTests {
        private PairId pairId;

        @BeforeEach
        void setUp() {
            pairId = new PairId();
        }

        @Test
        @DisplayName("Should create PairId entity")
        void testCreatePairId() {
            assertNotNull(pairId);
        }

        @Test
        @DisplayName("Should set and get firstId")
        void testSetGetFirstId() {
            pairId.setFirstId("ID1");
            assertEquals("ID1", pairId.getFirstId());
        }

        @Test
        @DisplayName("Should set and get secondId")
        void testSetGetSecondId() {
            pairId.setSecondId("ID2");
            assertEquals("ID2", pairId.getSecondId());
        }

        @Test
        @DisplayName("Should handle multiple PairIds")
        void testMultiplePairIds() {
            PairId pairId2 = new PairId();
            pairId.setFirstId("A");
            pairId.setSecondId("B");

            pairId2.setFirstId("C");
            pairId2.setSecondId("D");

            assertEquals("A", pairId.getFirstId());
            assertEquals("B", pairId.getSecondId());
            assertEquals("C", pairId2.getFirstId());
            assertEquals("D", pairId2.getSecondId());
        }
    }

    @Nested
    @DisplayName("Entity Relationship Tests")
    class EntityRelationshipTests {
        
        @Test
        @DisplayName("Should link Users to Groups through GroupMember")
        void testUsersGroupsRelationship() {
            Users user = new Users();
            Groups group = new Groups();
            GroupMember member = new GroupMember();

            user.setUserId("U1");
            group.setGroupId("G1");

            member.setUser(user);
            member.setGroup(group);

            assertEquals("U1", member.getUser().getUserId());
            assertEquals("G1", member.getGroup().getGroupId());
        }

        @Test
        @DisplayName("Should link Users to Tags through UserTag")
        void testUsersTagsRelationship() {
            Users user = new Users();
            Tags tag = new Tags();
            UserTag userTag = new UserTag();

            user.setUserId("U1");
            tag.setTagId("T1");

            userTag.setUser(user);
            userTag.setTag(tag);

            assertEquals("U1", userTag.getUser().getUserId());
            assertEquals("T1", userTag.getTag().getTagId());
        }

        @Test
        @DisplayName("Should link Groups to Courses")
        void testGroupsCoursesRelationship() {
            Groups group = new Groups();
            Courses course = new Courses();

            group.setGroupId("G1");
            course.setCourseId("C1");

            group.setCourse(course);

            assertEquals("C1", group.getCourse().getCourseId());
        }
    }
}
