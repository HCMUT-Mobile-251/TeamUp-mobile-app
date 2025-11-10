package com.teamup.main.dto.request;

import java.util.List;

import com.teamup.main.model.Courses;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SearchRequest {
    String name;
    String groupClass;
    String topicName;

    List<String> tagId;
    Courses course;
}
