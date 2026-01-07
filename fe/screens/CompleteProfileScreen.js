import React, { useState, useContext, useRef } from "react";
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
import { updateUser } from "../src/api/userService";
import { AuthContext } from "../App";

export default function CompleteProfileScreen({ navigation }) {
  const { userId } = useContext(AuthContext);
  const [saving, setSaving] = useState(false);
  const [formData, setFormData] = useState({
    studentId: "",
    faculty: "",
    phoneNumber: "",
  });

  // Refs cho scroll và inputs
  const scrollViewRef = useRef(null);
  const inputRefs = useRef({});

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const validateForm = () => {
    if (!formData.studentId.trim()) {
      Alert.alert("Lỗi", "Vui lòng nhập MSSV");
      return false;
    }
    if (!formData.faculty.trim()) {
      Alert.alert("Lỗi", "Vui lòng nhập khoa");
      return false;
    }
    // Phone number is optional
    return true;
  };

  const handleComplete = async () => {
    if (!validateForm()) return;

    setSaving(true);
    try {
      const updateData = {
        studentId: formData.studentId,
        faculty: formData.faculty,
        phoneNumber: formData.phoneNumber || "",
      };

      console.log("Completing profile with data:", updateData);
      const response = await updateUser(userId, updateData);
      console.log("Complete profile response:", response);

      if (response.code === 200) {
        Alert.alert(
          "Hoàn tất!",
          "Thông tin của bạn đã được cập nhật.",
          [
            {
              text: "OK",
              onPress: () => {
                // Navigate back to Tabs
                navigation.navigate("Tabs", { screen: "Home" });
              },
            },
          ]
        );
      } else {
        Alert.alert("Lỗi", response.message || "Không thể cập nhật thông tin");
      }
    } catch (error) {
      console.error("Complete profile error:", error);
      Alert.alert(
        "Lỗi",
        error.response?.data?.message || "Có lỗi xảy ra khi cập nhật thông tin"
      );
    } finally {
      setSaving(false);
    }
  };

  const renderInput = (label, field, placeholder, keyboardType = "default", options = {}) => {
    const required = options.required || false;

    return (
      <View
        style={{ marginBottom: 12 }}
        onLayout={(event) => {
          const layout = event.nativeEvent.layout;
          inputRefs.current[field] = layout.y;
        }}
      >
        <Text style={{ marginBottom: 6, color: "#666", fontWeight: "600" }}>
          {label} {required && <Text style={{ color: "red" }}>*</Text>}
        </Text>
        <TextInput
          placeholder={placeholder}
          value={formData[field]}
          onChangeText={(value) => handleInputChange(field, value)}
          keyboardType={keyboardType}
          editable={!saving}
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
            borderColor: "#E2E8F0",
            borderRadius: radii.md,
            paddingHorizontal: 12,
            paddingVertical: 12,
            backgroundColor: colors.white,
            color: "#000",
          }}
        />
      </View>
    );
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : "height"}
      style={{ flex: 1, backgroundColor: colors.bg }}
      keyboardVerticalOffset={Platform.OS === "ios" ? 100 : 0}
    >
      <ScrollView
        ref={scrollViewRef}
        contentContainerStyle={{ padding: 16, paddingBottom: 200 }}
        showsVerticalScrollIndicator={false}
        keyboardShouldPersistTaps="handled"
        keyboardDismissMode="interactive"
      >
        <Text style={{ fontSize: 28, fontWeight: "900", marginBottom: 8, color: colors.primary }}>
          Chào mừng đến TeamUp! 👋
        </Text>
        <Text style={{ fontSize: 16, color: colors.subtext, marginBottom: 24, lineHeight: 24 }}>
          Vui lòng hoàn thiện thông tin cá nhân để bắt đầu sử dụng ứng dụng.
        </Text>

        {renderInput("MSSV", "studentId", "VD: 2211234", "numeric", { required: true })}
        {renderInput("Khoa", "faculty", "VD: Khoa Khoa học và Kỹ thuật Máy tính", "default", { required: true })}
        {renderInput("Số điện thoại", "phoneNumber", "VD: 0123456789 (không bắt buộc)", "phone-pad")}

        <TouchableOpacity
          style={{
            marginTop: 24,
            backgroundColor: saving ? colors.subtext : colors.primary,
            paddingVertical: 16,
            borderRadius: radii.md,
          }}
          onPress={handleComplete}
          disabled={saving}
        >
          {saving ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800", fontSize: 16 }}>
              Hoàn tất
            </Text>
          )}
        </TouchableOpacity>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
