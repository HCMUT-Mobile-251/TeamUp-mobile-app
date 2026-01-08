package com.teamup.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamup.main.exception.AppException;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.model.Courses;
import com.teamup.main.repository.CourseRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Comprehensive Unit Tests - Phase 3")
class CourseServiceTest {
    @Mock private CourseRepository courseRepository;
    @InjectMocks private CourseService courseService;

    private Courses testCourse;
    private Courses testCourse2;

    @BeforeEach
    void setUp() {
        testCourse = new Courses();
        testCourse.setCourseId("CS101");
        testCourse.setName("Introduction to Computer Science");

        testCourse2 = new Courses();
        testCourse2.setCourseId("CS102");
        testCourse2.setName("Data Structures");
    }

    // ============ FIND COURSE TESTS ============
    @Nested
    @DisplayName("Find Course Tests")
    class FindCourseTests {
        @Test
        @DisplayName("Should find course by ID successfully")
        void testFindCourseByIdSuccess() {
            when(courseRepository.findById("CS101")).thenReturn(Optional.of(testCourse));
            
            Courses result = courseService.findCourse("CS101");
            
            assertNotNull(result);
            assertEquals("CS101", result.getCourseId());
            assertEquals("Introduction to Computer Science", result.getName());
            verify(courseRepository).findById("CS101");
        }

        @Test
        @DisplayName("Should throw exception when course not found by ID")
        void testFindCourseByIdNotFound() {
            when(courseRepository.findById("INVALID")).thenReturn(Optional.empty());
            
            AppException exception = assertThrows(AppException.class,
                () -> courseService.findCourse("INVALID"));
            assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
        }
    }

    // ============ GET COURSE TESTS ============
    @Nested
    @DisplayName("Get Course Tests")
    class GetCourseTests {
        @Test
        @DisplayName("Should find course by course ID")
        void testGetCourseByCourseId() {
            List<Courses> courses = List.of(testCourse);
            when(courseRepository.findByCourseIdContainingIgnoreCase("CS101")).thenReturn(courses);
            
            List<Courses> result = courseService.getCourse("CS101");
            
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("CS101", result.get(0).getCourseId());
            verify(courseRepository).findByCourseIdContainingIgnoreCase("CS101");
        }

        @Test
        @DisplayName("Should find course by name when course ID not found")
        void testGetCourseByNameWhenIdNotFound() {
            when(courseRepository.findByCourseIdContainingIgnoreCase("Introduction")).thenReturn(List.of());
            List<Courses> courses = List.of(testCourse);
            when(courseRepository.findByNameContainingIgnoreCase("Introduction")).thenReturn(courses);
            
            List<Courses> result = courseService.getCourse("Introduction");
            
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(courseRepository).findByCourseIdContainingIgnoreCase("Introduction");
            verify(courseRepository).findByNameContainingIgnoreCase("Introduction");
        }

        @Test
        @DisplayName("Should handle case-insensitive search by course ID")
        void testGetCourseCaseInsensitiveId() {
            List<Courses> courses = List.of(testCourse);
            when(courseRepository.findByCourseIdContainingIgnoreCase("cs101")).thenReturn(courses);
            
            List<Courses> result = courseService.getCourse("cs101");
            
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should trim whitespace from search query")
        void testGetCourseTrimWhitespace() {
            List<Courses> courses = List.of(testCourse);
            when(courseRepository.findByCourseIdContainingIgnoreCase("CS101")).thenReturn(courses);
            
            List<Courses> result = courseService.getCourse("  CS101  ");
            
            assertEquals(1, result.size());
            verify(courseRepository).findByCourseIdContainingIgnoreCase("CS101");
        }

        @Test
        @DisplayName("Should return empty list when no courses found")
        void testGetCourseNotFound() {
            when(courseRepository.findByCourseIdContainingIgnoreCase("UNKNOWN")).thenReturn(List.of());
            when(courseRepository.findByNameContainingIgnoreCase("UNKNOWN")).thenReturn(List.of());
            
            List<Courses> result = courseService.getCourse("UNKNOWN");
            
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return multiple courses with similar IDs")
        void testGetCourseMultipleResults() {
            Courses course3 = new Courses();
            course3.setCourseId("CS101B");
            course3.setName("Intro to CS - Section B");
            
            List<Courses> courses = List.of(testCourse, course3);
            when(courseRepository.findByCourseIdContainingIgnoreCase("CS101")).thenReturn(courses);
            
            List<Courses> result = courseService.getCourse("CS101");
            
            assertEquals(2, result.size());
        }
    }

    // ============ GET COURSES TESTS ============
    @Nested
    @DisplayName("Get Courses Tests")
    class GetCoursesTests {
        @Test
        @DisplayName("Should retrieve all courses successfully")
        void testGetCoursesSuccess() {
            List<Courses> courses = List.of(testCourse, testCourse2);
            when(courseRepository.findAll()).thenReturn(courses);
            
            List<Courses> result = courseService.getCourses();
            
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(courseRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no courses exist")
        void testGetCoursesEmpty() {
            when(courseRepository.findAll()).thenReturn(List.of());
            
            List<Courses> result = courseService.getCourses();
            
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ============ CREATE COURSE TESTS ============
    @Nested
    @DisplayName("Create Course Tests")
    class CreateCourseTests {
        @Test
        @DisplayName("Should create single course successfully")
        void testCreateSingleCourseSuccess() {
            List<Courses> coursesToCreate = List.of(testCourse);
            when(courseRepository.saveAll(coursesToCreate)).thenReturn(coursesToCreate);
            
            List<Courses> result = courseService.createCourse(coursesToCreate);
            
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("CS101", result.get(0).getCourseId());
            verify(courseRepository).saveAll(coursesToCreate);
        }

        @Test
        @DisplayName("Should create multiple courses successfully")
        void testCreateMultipleCoursesSuccess() {
            List<Courses> coursesToCreate = List.of(testCourse, testCourse2);
            when(courseRepository.saveAll(coursesToCreate)).thenReturn(coursesToCreate);
            
            List<Courses> result = courseService.createCourse(coursesToCreate);
            
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(courseRepository).saveAll(coursesToCreate);
        }

        @Test
        @DisplayName("Should handle empty course list")
        void testCreateEmptyCourseList() {
            when(courseRepository.saveAll(List.of())).thenReturn(List.of());
            
            List<Courses> result = courseService.createCourse(List.of());
            
            assertTrue(result.isEmpty());
        }
    }

    // ============ UPDATE COURSE TESTS ============
    @Nested
    @DisplayName("Update Course Tests")
    class UpdateCourseTests {
        @Test
        @DisplayName("Should update existing course successfully")
        void testUpdateCourseSuccess() {
            Courses updatedCourse = new Courses();
            updatedCourse.setCourseId("CS101");
            updatedCourse.setName("Updated Name");
            
            when(courseRepository.findById("CS101")).thenReturn(Optional.of(testCourse));
            when(courseRepository.save(any(Courses.class))).thenReturn(updatedCourse);
            
            Courses result = courseService.updateCourse(updatedCourse);
            
            assertNotNull(result);
            verify(courseRepository).findById("CS101");
            verify(courseRepository).save(any(Courses.class));
        }

        @Test
        @DisplayName("Should throw exception when course not found on update")
        void testUpdateCourseNotFound() {
            when(courseRepository.findById("INVALID")).thenReturn(Optional.empty());
            
            Courses courseToUpdate = new Courses();
            courseToUpdate.setCourseId("INVALID");
            
            AppException exception = assertThrows(AppException.class,
                () -> courseService.updateCourse(courseToUpdate));
            assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update course name")
        void testUpdateCourseName() {
            testCourse.setName("New Name");
            when(courseRepository.findById("CS101")).thenReturn(Optional.of(testCourse));
            when(courseRepository.save(testCourse)).thenReturn(testCourse);
            
            Courses result = courseService.updateCourse(testCourse);
            
            assertNotNull(result);
            assertEquals("New Name", result.getName());
        }

        @Test
        @DisplayName("Should update course description")
        void testUpdateCourseNameAgain() {
            testCourse.setName("Another Updated Name");
            when(courseRepository.findById("CS101")).thenReturn(Optional.of(testCourse));
            when(courseRepository.save(testCourse)).thenReturn(testCourse);
            
            Courses result = courseService.updateCourse(testCourse);
            
            assertNotNull(result);
            assertEquals("Another Updated Name", result.getName());
        }
    }

    // ============ DELETE COURSE TESTS ============
    @Nested
    @DisplayName("Delete Course Tests")
    class DeleteCourseTests {
        @Test
        @DisplayName("Should delete existing course successfully")
        void testDeleteCourseSuccess() {
            when(courseRepository.findById("CS101")).thenReturn(Optional.of(testCourse));
            doNothing().when(courseRepository).deleteById("CS101");
            
            assertDoesNotThrow(() -> courseService.deleteCourse("CS101"));
            verify(courseRepository).deleteById("CS101");
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent course")
        void testDeleteCourseNotFound() {
            when(courseRepository.findById("INVALID")).thenReturn(Optional.empty());
            
            AppException exception = assertThrows(AppException.class,
                () -> courseService.deleteCourse("INVALID"));
            assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
            verify(courseRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should delete course by correct ID")
        void testDeleteCourseByCorrectId() {
            when(courseRepository.findById("CS102")).thenReturn(Optional.of(testCourse2));
            doNothing().when(courseRepository).deleteById("CS102");
            
            assertDoesNotThrow(() -> courseService.deleteCourse("CS102"));
            verify(courseRepository).deleteById("CS102");
        }
    }
}
