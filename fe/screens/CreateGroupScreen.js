import React, { useState, useEffect } from "react";
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
import { createGroup } from "../src/api/groupService";
import { searchCourses } from "../src/api/courseService";

export default function CreateGroupScreen({ navigation }) {
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    courseId: "",
    courseName: "",
    groupClass: "",
    semester: "",
    name: "",
    topicName: "",
    maxMembers: "",
    description: "",
  });

  const [courses, setCourses] = useState([]);
  const [courseSearching, setCourseSearching] = useState(false);

  // Search course when courseId changes
  useEffect(() => {
    if (formData.courseId.length >= 3) {
      searchCourse(formData.courseId);
    }
  }, [formData.courseId]);

  const searchCourse = async (query) => {
    setCourseSearching(true);
    try {
      const response = await searchCourses(query);
      if (response.code === 200 && response.result) {
        setCourses(response.result);
        // Auto-fill if exact match
        const exactMatch = response.result.find(
          (c) => c.courseId.toLowerCase() === query.toLowerCase()
        );
        if (exactMatch) {
          setFormData((prev) => ({ ...prev, courseName: exactMatch.name }));
        }
      }
    } catch (error) {
      console.error("Course search error:", error);
    } finally {
      setCourseSearching(false);
    }
  };

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
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
      // TODO: Replace with actual userId from auth context
      const currentUserId = "2211093";

      const groupData = {
        courseId: formData.courseId,
        name: formData.name,
        topicName: formData.topicName,
        description: formData.description || "",
        maxMembers: parseInt(formData.maxMembers),
        groupClass: formData.groupClass || "",
        leaderId: currentUserId, // User hiện tại sẽ là leader
      };

      console.log("Creating group with data:", groupData);
      const response = await createGroup(groupData);
      console.log("Create group response:", response);

      if (response.code === 200 || response.code === 201) {
        Alert.alert(
          "Thành công",
          "Tạo nhóm thành công!",
          [
            {
              text: "OK",
              onPress: () => {
                // Navigate to home and reset the stack
                navigation.navigate("Main", { screen: "Home" });
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

      {renderInput("Mã môn học", "courseId", "VD: CO3001", "default")}
      {renderInput("Tên môn học", "courseName", "Sẽ tự động điền khi nhập mã môn", "default")}
      {renderInput("Mã lớp", "groupClass", "VD: L01 (không bắt buộc)", "default")}
      {renderInput("Học kỳ", "semester", "VD: 251", "numeric")}
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
