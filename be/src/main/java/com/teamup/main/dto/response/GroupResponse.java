package com.teamup.main.dto.response;

import java.util.Set;

import com.teamup.main.model.Courses;
import com.teamup.main.model.GroupTag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class GroupResponse {
    String groupId;
    String name;
    String groupClass;
    String topicName;
    int semester;

    Courses course;
    Set<GroupTag> groupTags;

    // để ui hiện thị nút join hoặc view
    Boolean isMember;

    // Alias for test compatibility
    public boolean isIsMember() {
        return isMember != null && isMember;
    }

    public void setIsMember(Boolean isMember) {
        this.isMember = isMember;
    }
}
