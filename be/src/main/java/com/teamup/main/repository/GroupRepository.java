package com.teamup.main.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.teamup.main.enums.GroupStatus;
import com.teamup.main.model.Groups;

@Repository
public interface GroupRepository extends JpaRepository<Groups, String> {
        // Custom query methods can be defined here if needed
        List<Groups> findByTopicNameContainingIgnoreCase(String topicName);

        List<Groups> findByNameContainingIgnoreCase(String name);

        List<Groups> findByGroupClassContainingIgnoreCase(String groupClass);

        Page<Groups> findBySemesterAndGroupTags_Id_SecondIdIn(
                        int semester,
                        List<String> tagIds,
                        Pageable pageable);

        boolean existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                        String userId,
                        String groupId,
                        GroupStatus status);

        List<Groups> findBySemester(int semester);

        Page<Groups> findBySemester(int semester, Pageable pageable);

        // Tìm group theo cả id hoặc name của course
        List<Groups> findByCourse_CourseIdOrCourse_NameContainingIgnoreCase(String courseId, String courseName);

        // Tìm groups mà user là member, theo semester và course
        @Query("""
                            SELECT g
                            FROM Groups g
                            WHERE g.semester = :semester
                              AND g.course.courseId = :courseId
                              AND EXISTS (
                                    SELECT gm
                                    FROM g.groupMembers gm
                                    WHERE gm.id.secondId = :userId
                                      AND gm.status = com.teamup.main.enums.GroupStatus.JOINED
                              )
                        """)
        Groups findGroupWithJoinedMember(
                        int semester,
                        String courseId,
                        String userId);
}