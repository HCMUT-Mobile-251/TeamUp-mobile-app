package com.teamup.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamup.main.dto.request.SearchRequest;
import com.teamup.main.dto.response.GroupResponse;
import com.teamup.main.enums.GroupStatus;
import com.teamup.main.mapper.GroupMapper;
import com.teamup.main.model.Courses;
import com.teamup.main.model.Groups;
import com.teamup.main.repository.GroupRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchService Comprehensive Unit Tests - Phase 4")
class SearchServiceTest {
    @Mock private GroupRepository groupRepository;
    @Mock private GroupService groupService;
    @Mock private GroupMapper groupMapper;
    @Mock private CourseService courseService;
    @InjectMocks private SearchService searchService;

    private Groups testGroup1;
    private Groups testGroup2;
    private Groups testGroup3;
    private Courses testCourse;
    private GroupResponse groupResponse1;
    private GroupResponse groupResponse2;

    @BeforeEach
    void setUp() {
        // Setup test courses
        testCourse = new Courses();
        testCourse.setCourseId("CS101");
        testCourse.setName("Introduction to Computer Science");

        // Setup test groups
        testGroup1 = new Groups();
        testGroup1.setGroupId("group1");
        testGroup1.setName("AI Study Group");
        testGroup1.setTopicName("Artificial Intelligence");
        testGroup1.setGroupClass("Class A");
        testGroup1.setCourse(testCourse);
        testGroup1.setSemester(20241);
        testGroup1.setGroupTags(new HashSet<>());

        testGroup2 = new Groups();
        testGroup2.setGroupId("group2");
        testGroup2.setName("Data Science Group");
        testGroup2.setTopicName("Machine Learning");
        testGroup2.setGroupClass("Class B");
        testGroup2.setCourse(testCourse);
        testGroup2.setSemester(20241);
        testGroup2.setGroupTags(new HashSet<>());

        testGroup3 = new Groups();
        testGroup3.setGroupId("group3");
        testGroup3.setName("Web Development");
        testGroup3.setTopicName("React and Spring");
        testGroup3.setGroupClass("Class C");
        testGroup3.setCourse(testCourse);
        testGroup3.setSemester(20242);
        testGroup3.setGroupTags(new HashSet<>());

        // Setup group responses
        groupResponse1 = GroupResponse.builder()
            .groupId("group1")
            .name("AI Study Group")
            .groupClass("Class A")
            .topicName("Artificial Intelligence")
            .course(testCourse)
            .groupTags(new HashSet<>())
            .isMember(false)
            .build();

        groupResponse2 = GroupResponse.builder()
            .groupId("group2")
            .name("Data Science Group")
            .groupClass("Class B")
            .topicName("Machine Learning")
            .course(testCourse)
            .groupTags(new HashSet<>())
            .isMember(false)
            .build();
    }

    // ============ ADVANCE SEARCH GROUPS TESTS ============
    @Nested
    @DisplayName("Advance Search Groups Tests")
    class AdvanceSearchGroupsTests {
        @Test
        @DisplayName("Should return all groups for empty search request")
        void testAdvanceSearchEmptyRequest() {
            SearchRequest request = new SearchRequest();
            request.setUserId("user123");
            request.setCourse(null);
            request.setName(null);
            request.setTopicName(null);
            request.setGroupClass(null);

            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findBySemester(20241)).thenReturn(List.of(testGroup1, testGroup2));
            when(groupMapper.toSearchGroup(testGroup1)).thenReturn(groupResponse1);
            when(groupMapper.toSearchGroup(testGroup2)).thenReturn(groupResponse2);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group1", GroupStatus.JOINED)).thenReturn(true);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group2", GroupStatus.JOINED)).thenReturn(false);

            List<GroupResponse> result = searchService.advanceSearchGroups(request);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.get(0).getIsMember());
            assertFalse(result.get(1).getIsMember());
            verify(groupRepository).findBySemester(20241);
        }

        @Test
        @DisplayName("Should filter groups by course ID")
        void testAdvanceSearchByCourseId() {
            Courses course = new Courses();
            course.setCourseId("CS101");
            course.setName("Introduction to Computer Science");

            SearchRequest request = new SearchRequest();
            request.setUserId("user123");
            request.setCourse(course);
            request.setName(null);
            request.setTopicName(null);
            request.setGroupClass(null);

            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findBySemester(20241)).thenReturn(List.of(testGroup1, testGroup2));
            when(courseService.findCourse("CS101")).thenReturn(course);
            when(groupMapper.toSearchGroup(testGroup1)).thenReturn(groupResponse1);
            when(groupMapper.toSearchGroup(testGroup2)).thenReturn(groupResponse2);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group1", GroupStatus.JOINED)).thenReturn(true);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group2", GroupStatus.JOINED)).thenReturn(false);

            List<GroupResponse> result = searchService.advanceSearchGroups(request);

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(courseService).findCourse("CS101");
        }

        @Test
        @DisplayName("Should filter groups by course name")
        void testAdvanceSearchByCourseNameWhenIdEmpty() {
            Courses course = new Courses();
            course.setCourseId("");
            course.setName("Introduction to Computer Science");

            SearchRequest request = new SearchRequest();
            request.setUserId("user123");
            request.setCourse(course);
            request.setName(null);
            request.setTopicName(null);
            request.setGroupClass(null);

            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findBySemester(20241)).thenReturn(List.of(testGroup1, testGroup2));
            when(groupMapper.toSearchGroup(testGroup1)).thenReturn(groupResponse1);
            when(groupMapper.toSearchGroup(testGroup2)).thenReturn(groupResponse2);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group1", GroupStatus.JOINED)).thenReturn(true);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group2", GroupStatus.JOINED)).thenReturn(false);

            List<GroupResponse> result = searchService.advanceSearchGroups(request);

            assertNotNull(result);
            verify(groupRepository).findBySemester(20241);
        }

        @Test
        @DisplayName("Should filter groups by name (case-insensitive)")
        void testAdvanceSearchByName() {
            SearchRequest request = new SearchRequest();
            request.setUserId("user123");
            request.setCourse(null);
            request.setName("AI");
            request.setTopicName(null);
            request.setGroupClass(null);

            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findBySemester(20241)).thenReturn(List.of(testGroup1, testGroup2));
            when(groupMapper.toSearchGroup(testGroup1)).thenReturn(groupResponse1);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group1", GroupStatus.JOINED)).thenReturn(true);

            List<GroupResponse> result = searchService.advanceSearchGroups(request);

            assertNotNull(result);
            verify(groupRepository).findBySemester(20241);
        }

        @Test
        @DisplayName("Should filter groups by topic name (case-insensitive)")
        void testAdvanceSearchByTopicName() {
            SearchRequest request = new SearchRequest();
            request.setUserId("user123");
            request.setCourse(null);
            request.setName(null);
            request.setTopicName("Machine Learning");
            request.setGroupClass(null);

            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findBySemester(20241)).thenReturn(List.of(testGroup1, testGroup2));
            when(groupMapper.toSearchGroup(testGroup2)).thenReturn(groupResponse2);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group2", GroupStatus.JOINED)).thenReturn(false);

            List<GroupResponse> result = searchService.advanceSearchGroups(request);

            assertNotNull(result);
            verify(groupRepository).findBySemester(20241);
        }

        @Test
        @DisplayName("Should filter groups by group class (case-insensitive)")
        void testAdvanceSearchByGroupClass() {
            SearchRequest request = new SearchRequest();
            request.setUserId("user123");
            request.setCourse(null);
            request.setName(null);
            request.setTopicName(null);
            request.setGroupClass("Class A");

            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findBySemester(20241)).thenReturn(List.of(testGroup1, testGroup2));
            when(groupMapper.toSearchGroup(testGroup1)).thenReturn(groupResponse1);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group1", GroupStatus.JOINED)).thenReturn(true);

            List<GroupResponse> result = searchService.advanceSearchGroups(request);

            assertNotNull(result);
            verify(groupRepository).findBySemester(20241);
        }

        @Test
        @DisplayName("Should return empty list when no groups match criteria")
        void testAdvanceSearchNoResults() {
            SearchRequest request = new SearchRequest();
            request.setUserId("user123");
            request.setCourse(null);
            request.setName("NonExistent");
            request.setTopicName(null);
            request.setGroupClass(null);

            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findBySemester(20241)).thenReturn(List.of(testGroup1, testGroup2));

            List<GroupResponse> result = searchService.advanceSearchGroups(request);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should apply multiple filters simultaneously")
        void testAdvanceSearchMultipleFilters() {
            Courses course = new Courses();
            course.setCourseId("CS101");
            course.setName("Introduction to Computer Science");

            SearchRequest request = new SearchRequest();
            request.setUserId("user123");
            request.setCourse(course);
            request.setName("AI");
            request.setTopicName("Artificial");
            request.setGroupClass("Class A");

            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findBySemester(20241)).thenReturn(List.of(testGroup1, testGroup2));
            when(courseService.findCourse("CS101")).thenReturn(course);
            when(groupMapper.toSearchGroup(testGroup1)).thenReturn(groupResponse1);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group1", GroupStatus.JOINED)).thenReturn(true);

            List<GroupResponse> result = searchService.advanceSearchGroups(request);

            assertNotNull(result);
            verify(courseService).findCourse("CS101");
        }
    }

    // ============ NORMAL SEARCH GROUP TESTS ============
    @Nested
    @DisplayName("Normal Search Group Tests")
    class NormalSearchGroupTests {
        @Test
        @DisplayName("Should search by topic name")
        void testNormalSearchByTopicName() {
            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findByTopicNameContainingIgnoreCase("AI"))
                    .thenReturn(List.of(testGroup1));
            when(groupRepository.findByNameContainingIgnoreCase("AI"))
                    .thenReturn(List.of());
            when(groupRepository.findByGroupClassContainingIgnoreCase("AI"))
                    .thenReturn(List.of());
            when(groupRepository.findByCourse_CourseIdOrCourse_NameContainingIgnoreCase("AI", "AI"))
                    .thenReturn(List.of());
            when(groupMapper.toSearchGroup(testGroup1)).thenReturn(groupResponse1);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group1", GroupStatus.JOINED)).thenReturn(true);

            List<GroupResponse> result = searchService.normalSearchGroup("AI", "user123");

            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.get(0).getIsMember());
        }

        @Test
        @DisplayName("Should search by group name")
        void testNormalSearchByGroupName() {
            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findByTopicNameContainingIgnoreCase("Data Science"))
                    .thenReturn(List.of());
            when(groupRepository.findByNameContainingIgnoreCase("Data Science"))
                    .thenReturn(List.of(testGroup2));
            when(groupRepository.findByGroupClassContainingIgnoreCase("Data Science"))
                    .thenReturn(List.of());
            when(groupRepository.findByCourse_CourseIdOrCourse_NameContainingIgnoreCase("Data Science", "Data Science"))
                    .thenReturn(List.of());
            when(groupMapper.toSearchGroup(testGroup2)).thenReturn(groupResponse2);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group2", GroupStatus.JOINED)).thenReturn(false);

            List<GroupResponse> result = searchService.normalSearchGroup("Data Science", "user123");

            assertNotNull(result);
            assertEquals(1, result.size());
            assertFalse(result.get(0).getIsMember());
        }

        @Test
        @DisplayName("Should search by group class")
        void testNormalSearchByGroupClass() {
            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findByTopicNameContainingIgnoreCase("Class A"))
                    .thenReturn(List.of());
            when(groupRepository.findByNameContainingIgnoreCase("Class A"))
                    .thenReturn(List.of());
            when(groupRepository.findByGroupClassContainingIgnoreCase("Class A"))
                    .thenReturn(List.of(testGroup1));
            when(groupRepository.findByCourse_CourseIdOrCourse_NameContainingIgnoreCase("Class A", "Class A"))
                    .thenReturn(List.of());
            when(groupMapper.toSearchGroup(testGroup1)).thenReturn(groupResponse1);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group1", GroupStatus.JOINED)).thenReturn(true);

            List<GroupResponse> result = searchService.normalSearchGroup("Class A", "user123");

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should search by course ID or name")
        void testNormalSearchByCourse() {
            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findByTopicNameContainingIgnoreCase("CS101"))
                    .thenReturn(List.of());
            when(groupRepository.findByNameContainingIgnoreCase("CS101"))
                    .thenReturn(List.of());
            when(groupRepository.findByGroupClassContainingIgnoreCase("CS101"))
                    .thenReturn(List.of());
            when(groupRepository.findByCourse_CourseIdOrCourse_NameContainingIgnoreCase("CS101", "CS101"))
                    .thenReturn(List.of(testGroup1, testGroup2));
            when(groupMapper.toSearchGroup(testGroup1)).thenReturn(groupResponse1);
            when(groupMapper.toSearchGroup(testGroup2)).thenReturn(groupResponse2);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group1", GroupStatus.JOINED)).thenReturn(true);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group2", GroupStatus.JOINED)).thenReturn(false);

            List<GroupResponse> result = searchService.normalSearchGroup("CS101", "user123");

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should filter by current semester only")
        void testNormalSearchFiltersBySemester() {
            testGroup3.setSemester(20242); // Different semester

            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findByTopicNameContainingIgnoreCase("React"))
                    .thenReturn(List.of(testGroup3));
            when(groupRepository.findByNameContainingIgnoreCase("React"))
                    .thenReturn(List.of());
            when(groupRepository.findByGroupClassContainingIgnoreCase("React"))
                    .thenReturn(List.of());
            when(groupRepository.findByCourse_CourseIdOrCourse_NameContainingIgnoreCase("React", "React"))
                    .thenReturn(List.of());

            List<GroupResponse> result = searchService.normalSearchGroup("React", "user123");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle case-insensitive search")
        void testNormalSearchCaseInsensitive() {
            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findByTopicNameContainingIgnoreCase("ai"))
                    .thenReturn(List.of(testGroup1));
            when(groupRepository.findByNameContainingIgnoreCase("ai"))
                    .thenReturn(List.of());
            when(groupRepository.findByGroupClassContainingIgnoreCase("ai"))
                    .thenReturn(List.of());
            when(groupRepository.findByCourse_CourseIdOrCourse_NameContainingIgnoreCase("ai", "ai"))
                    .thenReturn(List.of());
            when(groupMapper.toSearchGroup(testGroup1)).thenReturn(groupResponse1);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group1", GroupStatus.JOINED)).thenReturn(true);

            List<GroupResponse> result = searchService.normalSearchGroup("ai", "user123");

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no results found")
        void testNormalSearchNoResults() {
            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findByTopicNameContainingIgnoreCase("NonExistent"))
                    .thenReturn(List.of());
            when(groupRepository.findByNameContainingIgnoreCase("NonExistent"))
                    .thenReturn(List.of());
            when(groupRepository.findByGroupClassContainingIgnoreCase("NonExistent"))
                    .thenReturn(List.of());
            when(groupRepository.findByCourse_CourseIdOrCourse_NameContainingIgnoreCase("NonExistent", "NonExistent"))
                    .thenReturn(List.of());

            List<GroupResponse> result = searchService.normalSearchGroup("NonExistent", "user123");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should remove duplicates from multiple search criteria")
        void testNormalSearchRemovesDuplicates() {
            when(groupService.getCurrentSemester()).thenReturn(20241);
            when(groupRepository.findByTopicNameContainingIgnoreCase("AI"))
                    .thenReturn(List.of(testGroup1));
            when(groupRepository.findByNameContainingIgnoreCase("AI"))
                    .thenReturn(List.of(testGroup1)); // Same group
            when(groupRepository.findByGroupClassContainingIgnoreCase("AI"))
                    .thenReturn(List.of());
            when(groupRepository.findByCourse_CourseIdOrCourse_NameContainingIgnoreCase("AI", "AI"))
                    .thenReturn(List.of());
            when(groupMapper.toSearchGroup(testGroup1)).thenReturn(groupResponse1);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    "user123", "group1", GroupStatus.JOINED)).thenReturn(true);

            List<GroupResponse> result = searchService.normalSearchGroup("AI", "user123");

            assertNotNull(result);
            assertEquals(1, result.size()); // Should be 1, not 2
        }
    }
}
