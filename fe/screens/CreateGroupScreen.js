import React, { useState, useEffect, useContext } from "react";
import {
  View,
  Text,
  TextInput,
  ScrollView,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
} from "react-native";
import { colors, radii } from "../src/ui/theme";
import { createGroup, updateGroupTags } from "../src/api/groupService";
import { searchCourses } from "../src/api/courseService";
import { searchTags, createTag } from "../src/api/tagService";
import { AuthContext } from "../App";

export default function CreateGroupScreen({ navigation }) {
  const { userId } = useContext(AuthContext);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    courseId: "",
    courseName: "",
    semester: "",
    groupClass: "",
    name: "",
    topicName: "",
    maxMembers: "",
    description: "",
  });

  const [courses, setCourses] = useState([]);
  const [courseSearching, setCourseSearching] = useState(false);
  const [selectedCourse, setSelectedCourse] = useState(null);
  const [showCourseDropdown, setShowCourseDropdown] = useState(false);

  // Tag selection states
  const [selectedTags, setSelectedTags] = useState([]);
  const [tagSearchQuery, setTagSearchQuery] = useState("");
  const [tagSearchResults, setTagSearchResults] = useState([]);
  const [tagSearching, setTagSearching] = useState(false);

  // Search course when courseId changes
  useEffect(() => {
    if (formData.courseId.length >= 2) {
      searchCourse(formData.courseId);
    } else {
      setCourses([]);
      setShowCourseDropdown(false);
    }
  }, [formData.courseId]);

  const searchCourse = async (query) => {
    setCourseSearching(true);
    try {
      const response = await searchCourses(query);
      if (response.code === 200 && response.result) {
        setCourses(response.result);
        setShowCourseDropdown(response.result.length > 0);
      }
    } catch (error) {
      console.error("Course search error:", error);
    } finally {
      setCourseSearching(false);
    }
  };

  const handleSelectCourse = (course) => {
    // Calculate current semester
    const today = new Date();
    const year = parseInt(String(today.getFullYear()).substring(1));
    const month = today.getMonth() + 1;
    const semester = month > 8 ? 1 : month > 4 ? 3 : 2;
    const currentSemester = year * 10 + semester;

    setFormData((prev) => ({
      ...prev,
      courseId: course.courseId,
      courseName: course.name,
      semester: currentSemester.toString()
    }));
    setSelectedCourse(course);
    setShowCourseDropdown(false);
  };

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  // Tag search and selection handlers
  useEffect(() => {
    if (tagSearchQuery.trim().length >= 2) {
      handleTagSearch();
    } else {
      setTagSearchResults([]);
    }
  }, [tagSearchQuery]);

  const handleTagSearch = async () => {
    setTagSearching(true);
    try {
      const response = await searchTags(tagSearchQuery);
      if (response.code === 200 && response.result) {
        setTagSearchResults(response.result);
      }
    } catch (error) {
      console.error("Tag search error:", error);
    } finally {
      setTagSearching(false);
    }
  };

  const handleCreateNewTag = async () => {
    if (!tagSearchQuery.trim()) {
      Alert.alert("Thông báo", "Vui lòng nhập tên tag");
      return;
    }

    try {
      const response = await createTag(tagSearchQuery.trim());
      if (response?.code === 200 && response?.result) {
        const newTag = response.result;
        if (!selectedTags.some((tag) => tag.tagId === newTag.tagId)) {
          setSelectedTags([...selectedTags, newTag]);
        }
        setTagSearchQuery("");
        setTagSearchResults([]);
        Alert.alert("Thành công", "Đã tạo tag mới!");
      }
    } catch (error) {
      console.error("Create tag error:", error);
      Alert.alert("Lỗi", "Không thể tạo tag mới");
    }
  };

  const handleSelectTag = (tag) => {
    if (!selectedTags.some((t) => t.tagId === tag.tagId)) {
      setSelectedTags([...selectedTags, tag]);
    }
    setTagSearchQuery("");
    setTagSearchResults([]);
  };

  const handleRemoveTag = (tagId) => {
    setSelectedTags(selectedTags.filter((tag) => tag.tagId !== tagId));
  };

  const validateForm = () => {
    if (!formData.courseId.trim()) {
      Alert.alert("Lỗi", "Vui lòng nhập mã môn học");
      return false;
    }
    if (!formData.name.trim()) {
      Alert.alert("Lỗi", "Vui lòng nhập tên nhóm");
      return false;
    }
    if (!formData.topicName.trim()) {
      Alert.alert("Lỗi", "Vui lòng nhập tên đề tài");
      return false;
    }
    if (!formData.maxMembers || parseInt(formData.maxMembers) < 1) {
      Alert.alert("Lỗi", "Số lượng thành viên phải lớn hơn 0");
      return false;
    }
    return true;
  };

  const handleCreateGroup = async () => {
    if (!validateForm()) return;

    setLoading(true);
    try {
      const groupData = {
        courseId: formData.courseId,
        name: formData.name,
        topicName: formData.topicName,
        description: formData.description || "",
        maxMembers: parseInt(formData.maxMembers),
        groupClass: formData.groupClass || "",
        leaderId: userId, // User hiện tại sẽ là leader
      };

      console.log("Creating group with data:", groupData);
      const response = await createGroup(groupData);
      console.log("Create group response:", response);

      if (response.code === 200 || response.code === 201) {
        const createdGroup = response.result;

        // Update group tags if any tags selected
        if (selectedTags.length > 0) {
          try {
            await updateGroupTags(createdGroup.groupId, selectedTags);
            console.log("Group tags updated successfully");
          } catch (tagError) {
            console.error("Error updating group tags:", tagError);
            // Continue even if tag update fails
          }
        }

        Alert.alert(
          "Thành công",
          "Tạo nhóm thành công!",
          [
            {
              text: "OK",
              onPress: () => {
                // Navigate back to home tab and refresh
                navigation.navigate("Tabs", {
                  screen: "Home",
                  params: { refresh: true }
                });
              },
            },
          ]
        );
      } else {
        Alert.alert("Lỗi", response.message || "Không thể tạo nhóm");
      }
    } catch (error) {
      console.error("Create group error:", error);
      Alert.alert(
        "Lỗi",
        error.response?.data?.message || "Có lỗi xảy ra khi tạo nhóm"
      );
    } finally {
      setLoading(false);
    }
  };

  const renderInput = (label, field, placeholder, keyboardType = "default") => (
    <View style={{ marginBottom: 12 }}>
      <Text style={{ marginBottom: 6, color: "#666", fontWeight: "600" }}>
        {label} {["courseId", "name", "topicName", "maxMembers"].includes(field) && <Text style={{ color: "red" }}>*</Text>}
      </Text>
      <View
        style={{
          borderWidth: 1,
          borderColor: "#E2E8F0",
          borderRadius: radii.md,
          paddingHorizontal: 12,
          paddingVertical: 12,
          backgroundColor: colors.white,
        }}
      >
        <TextInput
          placeholder={placeholder}
          value={formData[field]}
          onChangeText={(value) => handleInputChange(field, value)}
          keyboardType={keyboardType}
          editable={!loading}
        />
      </View>
      {field === "courseId" && courseSearching && (
        <ActivityIndicator style={{ marginTop: 4 }} size="small" />
      )}
    </View>
  );

  return (
    <ScrollView contentContainerStyle={{ padding: 16 }} showsVerticalScrollIndicator={false}>
      <Text style={{ fontSize: 22, fontWeight: "800", marginBottom: 4 }}>
        Tạo nhóm mới
      </Text>
      <Text style={{ fontSize: 14, color: colors.subtext, marginBottom: 16 }}>
        Điền thông tin để tạo nhóm cho đề tài của bạn
      </Text>

      {/* Course selection with dropdown */}
      <View style={{ marginBottom: 12 }}>
        <Text style={{ marginBottom: 6, color: "#666", fontWeight: "600" }}>
          Mã môn học <Text style={{ color: "red" }}>*</Text>
        </Text>
        <View
          style={{
            borderWidth: 1,
            borderColor: "#E2E8F0",
            borderRadius: radii.md,
            paddingHorizontal: 12,
            paddingVertical: 12,
            backgroundColor: colors.white,
          }}
        >
          <TextInput
            placeholder="Nhập mã môn học (VD: CO3001)"
            value={formData.courseId}
            onChangeText={(value) => handleInputChange("courseId", value)}
            editable={!loading}
          />
        </View>
        {courseSearching && (
          <ActivityIndicator style={{ marginTop: 4 }} size="small" />
        )}
        {selectedCourse && (
          <Text style={{ marginTop: 4, fontSize: 13, color: colors.primary }}>
            ✓ {selectedCourse.name}
          </Text>
        )}
        {showCourseDropdown && courses.length > 0 && (
          <View
            style={{
              marginTop: 4,
              borderWidth: 1,
              borderColor: "#E2E8F0",
              borderRadius: radii.md,
              backgroundColor: colors.white,
              maxHeight: 200,
            }}
          >
            <ScrollView>
              {courses.map((course) => (
                <TouchableOpacity
                  key={course.courseId}
                  style={{
                    padding: 12,
                    borderBottomWidth: 1,
                    borderBottomColor: "#F1F5F9",
                  }}
                  onPress={() => handleSelectCourse(course)}
                >
                  <Text style={{ fontWeight: "600", fontSize: 14 }}>
                    {course.courseId}
                  </Text>
                  <Text style={{ fontSize: 12, color: colors.subtext, marginTop: 2 }}>
                    {course.name}
                  </Text>
                </TouchableOpacity>
              ))}
            </ScrollView>
          </View>
        )}
      </View>

      {/* Course Name - Disabled */}
      <View style={{ marginBottom: 12 }}>
        <Text style={{ marginBottom: 6, color: "#666", fontWeight: "600" }}>
          Tên môn học
        </Text>
        <View
          style={{
            borderWidth: 1,
            borderColor: "#E2E8F0",
            borderRadius: radii.md,
            paddingHorizontal: 12,
            paddingVertical: 12,
            backgroundColor: "#F8FAFC",
          }}
        >
          <TextInput
            placeholder="Sẽ tự động điền khi chọn môn học"
            value={formData.courseName}
            editable={false}
            style={{ color: "#64748B" }}
          />
        </View>
      </View>

      {/* Semester - Disabled */}
      <View style={{ marginBottom: 12 }}>
        <Text style={{ marginBottom: 6, color: "#666", fontWeight: "600" }}>
          Học kỳ
        </Text>
        <View
          style={{
            borderWidth: 1,
            borderColor: "#E2E8F0",
            borderRadius: radii.md,
            paddingHorizontal: 12,
            paddingVertical: 12,
            backgroundColor: "#F8FAFC",
          }}
        >
          <TextInput
            placeholder="Tự động tính theo học kỳ hiện tại"
            value={formData.semester}
            editable={false}
            style={{ color: "#64748B" }}
          />
        </View>
      </View>

      {renderInput("Mã lớp", "groupClass", "VD: L01 (không bắt buộc)", "default")}
      {renderInput("Tên nhóm", "name", "VD: Nhóm 1", "default")}
      {renderInput("Tên đề tài", "topicName", "VD: Xây dựng ứng dụng...", "default")}
      {renderInput("Số lượng thành viên", "maxMembers", "VD: 5", "numeric")}

      <View style={{ marginBottom: 12 }}>
        <Text style={{ marginBottom: 6, color: "#666", fontWeight: "600" }}>
          Miêu tả đề tài
        </Text>
        <View
          style={{
            borderWidth: 1,
            borderColor: "#E2E8F0",
            borderRadius: radii.md,
            paddingHorizontal: 12,
            paddingVertical: 12,
            backgroundColor: colors.white,
          }}
        >
          <TextInput
            placeholder="Mô tả chi tiết về đề tài..."
            value={formData.description}
            onChangeText={(value) => handleInputChange("description", value)}
            multiline
            numberOfLines={4}
            textAlignVertical="top"
            editable={!loading}
          />
        </View>
      </View>

      {/* Tag Selection */}
      <View style={{ marginBottom: 12 }}>
        <Text style={{ marginBottom: 6, color: "#666", fontWeight: "600" }}>
          Tags liên quan (không bắt buộc)
        </Text>

        {/* Selected Tags */}
        {selectedTags.length > 0 && (
          <View style={{ flexDirection: "row", flexWrap: "wrap", marginBottom: 8 }}>
            {selectedTags.map((tag) => (
              <View
                key={tag.tagId}
                style={{
                  flexDirection: "row",
                  alignItems: "center",
                  backgroundColor: colors.primary,
                  paddingHorizontal: 12,
                  paddingVertical: 6,
                  borderRadius: 16,
                  marginRight: 8,
                  marginBottom: 8,
                }}
              >
                <Text style={{ color: "#fff", fontSize: 13, marginRight: 6 }}>
                  {tag.name}
                </Text>
                <TouchableOpacity onPress={() => handleRemoveTag(tag.tagId)}>
                  <Text style={{ color: "#fff", fontSize: 16, fontWeight: "bold" }}>×</Text>
                </TouchableOpacity>
              </View>
            ))}
          </View>
        )}

        {/* Search Tags */}
        <View
          style={{
            borderWidth: 1,
            borderColor: "#E2E8F0",
            borderRadius: radii.md,
            paddingHorizontal: 12,
            paddingVertical: 12,
            backgroundColor: colors.white,
            marginBottom: 8,
          }}
        >
          <TextInput
            placeholder="Tìm kiếm hoặc tạo tag mới..."
            value={tagSearchQuery}
            onChangeText={setTagSearchQuery}
            editable={!loading}
          />
        </View>

        {tagSearching && (
          <ActivityIndicator style={{ marginBottom: 8 }} size="small" />
        )}

        {/* Tag Search Results */}
        {tagSearchResults.length > 0 && (
          <View
            style={{
              borderWidth: 1,
              borderColor: "#E2E8F0",
              borderRadius: radii.md,
              backgroundColor: colors.white,
              marginBottom: 8,
              maxHeight: 150,
            }}
          >
            <ScrollView>
              {tagSearchResults.map((tag) => (
                <TouchableOpacity
                  key={tag.tagId}
                  style={{
                    padding: 12,
                    borderBottomWidth: 1,
                    borderBottomColor: "#F1F5F9",
                  }}
                  onPress={() => handleSelectTag(tag)}
                >
                  <Text style={{ fontSize: 14 }}>{tag.name}</Text>
                </TouchableOpacity>
              ))}
            </ScrollView>
          </View>
        )}

        {/* Create New Tag Button */}
        {tagSearchQuery.trim() && (
          <TouchableOpacity
            style={{
              borderWidth: 1,
              borderColor: colors.primary,
              borderRadius: radii.md,
              paddingVertical: 10,
              paddingHorizontal: 12,
              marginBottom: 8,
            }}
            onPress={handleCreateNewTag}
          >
            <Text style={{ color: colors.primary, textAlign: "center", fontWeight: "600" }}>
              + Tạo tag mới "{tagSearchQuery}"
            </Text>
          </TouchableOpacity>
        )}
      </View>

      <TouchableOpacity
        style={{
          marginTop: 8,
          backgroundColor: loading ? colors.subtext : colors.primary,
          paddingVertical: 16,
          borderRadius: radii.md,
        }}
        onPress={handleCreateGroup}
        disabled={loading}
      >
        {loading ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800", fontSize: 16 }}>
            Tạo nhóm
          </Text>
        )}
      </TouchableOpacity>
    </ScrollView>
  );
}