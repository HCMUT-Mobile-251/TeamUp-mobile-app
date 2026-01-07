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
import { getUserById, updateUser } from "../src/api/userService";
import { AuthContext } from "../App";

export default function EditProfileScreen({ navigation }) {
  const { userId } = useContext(AuthContext);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [formData, setFormData] = useState({
    studentId: "",
    faculty: "",
    phoneNumber: "",
  });

  // Refs cho scroll và inputs
  const scrollViewRef = useRef(null);
  const inputRefs = useRef({});

  useEffect(() => {
    loadUserData();
  }, []);

  const loadUserData = async () => {
    setLoading(true);
    try {
      const response = await getUserById(userId);
      if (response?.code === 200 && response?.result) {
        const user = response.result;
        setFormData({
          studentId: user.studentId || "",
          faculty: user.faculty || "",
          phoneNumber: user.phoneNumber || "",
        });
      }
    } catch (error) {
      console.error("Load user error:", error);
      Alert.alert("Lỗi", "Không thể tải thông tin người dùng");
    } finally {
      setLoading(false);
    }
  };

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

  const handleSave = async () => {
    if (!validateForm()) return;

    setSaving(true);
    try {
      const updateData = {
        studentId: formData.studentId,
        faculty: formData.faculty,
        phoneNumber: formData.phoneNumber || "",
      };

      console.log("Updating user with data:", updateData);
      const response = await updateUser(userId, updateData);
      console.log("Update response:", response);

      if (response.code === 200) {
        Alert.alert(
          "Thành công",
          "Cập nhật thông tin thành công!",
          [
            {
              text: "OK",
              onPress: () => navigation.goBack(),
            },
          ]
        );
      } else {
        Alert.alert("Lỗi", response.message || "Không thể cập nhật thông tin");
      }
    } catch (error) {
      console.error("Update user error:", error);
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
    const editable = options.editable !== false; // Default to true

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
          editable={!saving && editable}
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
            borderColor: editable ? "#E2E8F0" : "#F1F5F9",
            borderRadius: radii.md,
            paddingHorizontal: 12,
            paddingVertical: 12,
            backgroundColor: editable ? colors.white : "#F8FAFC",
            color: editable ? "#000" : "#94A3B8",
          }}
        />
      </View>
    );
  };

  if (loading) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center", backgroundColor: colors.bg }}>
        <ActivityIndicator size="large" color={colors.primary} />
        <Text style={{ marginTop: 12, color: colors.subtext }}>
          Đang tải thông tin...
        </Text>
      </View>
    );
  }

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
        <Text style={{ fontSize: 22, fontWeight: "800", marginBottom: 4 }}>
          Chỉnh sửa thông tin
        </Text>
        <Text style={{ fontSize: 14, color: colors.subtext, marginBottom: 16 }}>
          Cập nhật thông tin cá nhân của bạn
        </Text>

        {renderInput("MSSV", "studentId", "VD: 2211234", "numeric", { required: true })}
        {renderInput("Khoa", "faculty", "VD: Khoa Khoa học và Kỹ thuật Máy tính", "default", { required: true })}
        {renderInput("Số điện thoại", "phoneNumber", "VD: 0123456789 (không bắt buộc)", "phone-pad")}

        <TouchableOpacity
          style={{
            marginTop: 8,
            backgroundColor: saving ? colors.subtext : colors.primary,
            paddingVertical: 16,
            borderRadius: radii.md,
          }}
          onPress={handleSave}
          disabled={saving}
        >
          {saving ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800", fontSize: 16 }}>
              Lưu thông tin
            </Text>
          )}
        </TouchableOpacity>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
