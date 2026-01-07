import React, { useState, useEffect, useContext, useRef } from "react";
import {
  View,
  Text,
  TextInput,
  ScrollView,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import { colors, radii } from "../src/ui/theme";
import { createGroup } from "../src/api/groupService";
import { searchCourses } from "../src/api/courseService";
import { AuthContext } from "../App";

// Hàm tính học kỳ hiện tại
const getCurrentSemester = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1; // 0-indexed

  // Học kỳ 1: tháng 9-12 (năm trước) -> 231, 241, 251
  // Học kỳ 2: tháng 1-5 (năm này) -> 232, 242, 252
  // Học kỳ hè: tháng 6-8 (năm này) -> 233, 243, 253

  const lastTwoDigits = year % 100; // 2024 -> 24

  if (month >= 9 && month <= 12) {
    // Học kỳ 1
    return `${lastTwoDigits}1`;
  } else if (month >= 1 && month <= 5) {
    // Học kỳ 2
    return `${lastTwoDigits}2`;
  } else {
    // Học kỳ hè (6-8)
    return `${lastTwoDigits}3`;
  }
};

export default function CreateGroupScreen({ navigation }) {
  const { userId } = useContext(AuthContext);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    courseId: "",
    courseName: "",
    groupClass: "",
    semester: getCurrentSemester(), // Tự động tính học kỳ
    name: "",
    topicName: "",
    maxMembers: "",
    description: "",
  });

  const [courses, setCourses] = useState([]);
  const [courseSearching, setCourseSearching] = useState(false);

  // Refs cho scroll và inputs
  const scrollViewRef = useRef(null);
  const inputRefs = useRef({});

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
        Alert.alert(
          "Thành công",
          "Tạo nhóm thành công!",
          [
            {
              text: "OK",
              onPress: () => {
                // Navigate to home and reset the stack
                navigation.navigate("Tabs", { screen: "Home" });
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

  const renderInput = (label, field, placeholder, keyboardType = "default", options = {}) => {
    const isDisabled = options.disabled || false;
    const isEditable = !loading && !isDisabled;
    const autoFocus = options.autoFocus || false;

    return (
      <View
        style={{ marginBottom: 12 }}
        onLayout={(event) => {
          const layout = event.nativeEvent.layout;
          inputRefs.current[field] = layout.y;
        }}
      >
        <Text style={{ marginBottom: 6, color: "#666", fontWeight: "600" }}>
          {label} {["courseId", "name", "topicName", "maxMembers"].includes(field) && <Text style={{ color: "red" }}>*</Text>}
        </Text>
        <TextInput
          placeholder={placeholder}
          value={formData[field]}
          onChangeText={(value) => handleInputChange(field, value)}
          keyboardType={keyboardType}
          editable={isEditable}
          autoFocus={autoFocus}
          onFocus={() => {
            // Scroll đến vị trí của input khi focus
            if (scrollViewRef.current && inputRefs.current[field]) {
              scrollViewRef.current.scrollTo({
                y: inputRefs.current[field] - 100,
                animated: true,
              });
            }
          }}
          style={{
            borderWidth: 1,
            borderColor: isDisabled ? "#F1F5F9" : "#E2E8F0",
            borderRadius: radii.md,
            paddingHorizontal: 12,
            paddingVertical: 12,
            backgroundColor: isDisabled ? "#F8FAFC" : colors.white,
            color: isDisabled ? "#94A3B8" : "#000",
          }}
        />
        {field === "courseId" && courseSearching && (
          <ActivityIndicator style={{ marginTop: 4 }} size="small" />
        )}
      </View>
    );
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : "height"}
      style={{ flex: 1 }}
      keyboardVerticalOffset={Platform.OS === "ios" ? 100 : 0}
    >
      <ScrollView
        ref={scrollViewRef}
        contentContainerStyle={{ padding: 16, paddingBottom: 200 }}
        showsVerticalScrollIndicator={false}
        keyboardShouldPersistTaps="handled"
        keyboardDismissMode="interactive"
      >
        <Text style={{ fontSize: 22, fontWeight: "800", marginBottom: 4 }}>
          Tạo nhóm mới
        </Text>
        <Text style={{ fontSize: 14, color: colors.subtext, marginBottom: 16 }}>
          Điền thông tin để tạo nhóm cho đề tài của bạn
        </Text>

        {renderInput("Mã môn học", "courseId", "VD: CO3001", "default", { autoFocus: true })}
        {renderInput("Tên môn học", "courseName", "Tự động lấy từ mã môn học", "default", { disabled: true })}
        {renderInput("Mã lớp", "groupClass", "VD: L01 (không bắt buộc)", "default")}
        {renderInput("Học kỳ", "semester", "Tự động theo thời gian hiện tại", "numeric", { disabled: true })}
        {renderInput("Tên nhóm", "name", "VD: Nhóm 1", "default")}
        {renderInput("Tên đề tài", "topicName", "VD: Xây dựng ứng dụng...", "default")}
      {renderInput("Số lượng thành viên", "maxMembers", "VD: 5", "numeric")}

      <View
        style={{ marginBottom: 12 }}
        onLayout={(event) => {
          const layout = event.nativeEvent.layout;
          inputRefs.current["description"] = layout.y;
        }}
      >
        <Text style={{ marginBottom: 6, color: "#666", fontWeight: "600" }}>
          Miêu tả đề tài
        </Text>
        <TextInput
          placeholder="Mô tả chi tiết về đề tài..."
          value={formData.description}
          onChangeText={(value) => handleInputChange("description", value)}
          multiline
          numberOfLines={4}
          textAlignVertical="top"
          editable={!loading}
          onFocus={() => {
            // Scroll đến vị trí của input khi focus
            if (scrollViewRef.current && inputRefs.current["description"]) {
              scrollViewRef.current.scrollTo({
                y: inputRefs.current["description"] - 100,
                animated: true,
              });
            }
          }}
          style={{
            borderWidth: 1,
            borderColor: "#E2E8F0",
            borderRadius: radii.md,
            paddingHorizontal: 12,
            paddingVertical: 12,
            backgroundColor: colors.white,
            minHeight: 100,
          }}
        />
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
    </KeyboardAvoidingView>
  );
}
