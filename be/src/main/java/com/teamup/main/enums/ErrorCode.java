package com.teamup.main.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNKNOWN_ERROR("Lỗi không xác định", 500),
    INTERNET_ERROR("Lỗi kết nối Internet", 503),
    BAD_REQUEST("Yêu cầu không hợp lệ", 400),
    BAD_GATEWAY("Lỗi máy chủ", 502),

    EMAIL_ALREADY_EXISTS("Email đã tồn tại!", 409),
    EMAIL_NOT_AUTHORITY("Email không thuộc tổ chức HCMUT!", 409),
    INVALID_CREDENTIALS("Thông tin đăng nhập không hợp lệ!", 401),
    USER_NOT_FOUND("Người dùng không tồn tại!", 404),
    GROUP_NOT_FOUND("Nhóm không tồn tại!", 404),
    COURSE_NOT_FOUND("Khóa học không tồn tại!", 404),
    TAG_NOT_FOUND("Tag không tồn tại!", 404),
    USER_NOT_IN_GROUP("Người dùng không thuộc nhóm!", 404),
    GROUP_FULL("Nhóm đã đầy!", 409),
    NO_LEADER("Vui lòng đổi leader để rời nhóm!", 409),
    ADJUST_MAX_MEMBER("Vui lòng điều chỉnh lại thành viên để chỉnh trường maxMembers!", 409),
    USER_JOINED_ANOTHER_GROUP_SAME_COURSE("Người dùng đã tham gia nhóm khác với môn học tương tự trong kỳ này!", 409),
    USER_ALREADY_IN_GROUP("Người dùng đã ở trong nhóm!", 409),

    NOT_MATCH_ANY("Không tìm thấy kết quả phù hợp", 200);

    String message;
    int code;
}