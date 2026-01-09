import React, { useState, useCallback } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
  ScrollView,
  SafeAreaView,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import { colors, radii } from "../src/ui/theme";
import { updateUser } from "../src/api/userService";

// Define InputField outside component to prevent re-renders
const InputField = React.memo(({ label, value, onChangeText, placeholder, keyboardType = "default", editable = true, loading = false }) => (
  <View style={{ marginBottom: 16 }}>
    <Text style={{ marginBottom: 8, fontSize: 14, fontWeight: "600", color: "#333" }}>
      {label}
    </Text>
    <TextInput
      style={{
        borderWidth: 1,
        borderColor: "#E2E8F0",
        borderRadius: radii.md,
        paddingHorizontal: 12,
        paddingVertical: 14,
        fontSize: 15,
        backgroundColor: editable ? "#fff" : "#F8FAFC",
        color: editable ? colors.text : "#64748B",
      }}
      value={value}
      onChangeText={onChangeText}
      placeholder={placeholder}
      placeholderTextColor="#94A3B8"
      keyboardType={keyboardType}
      editable={editable && !loading}
      autoCorrect={false}
      autoCapitalize={keyboardType === "numeric" || keyboardType === "phone-pad" ? "none" : "sentences"}
    />
  </View>
));

export default function EditProfileScreen({ route, navigation }) {
  const { user } = route.params;

  const [formData, setFormData] = useState({
    studentId: user?.studentId || "",
    phoneNumber: user?.phoneNumber || "",
    faculty: user?.faculty || "",
  });

  const [loading, setLoading] = useState(false);

  // Full name from Google (not editable)
  const fullName = `${user?.firstName || ""} ${user?.lastName || ""}`.trim();

  const handleInputChange = useCallback((field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  }, []);

  const handleSave = async () => {
    setLoading(true);
    try {
      const response = await updateUser(user.userId, formData);
      if (response.code === 200) {
        Alert.alert("Thành công", "Đã cập nhật thông tin cá nhân!", [
          {
            text: "OK",
            onPress: () => {
              // Navigate back to profile and trigger refresh
              navigation.navigate("Tabs", {
                screen: "Profile",
                params: { refresh: true }
              });
            },
          },
        ]);
      } else {
        Alert.alert("Lỗi", response.message || "Không thể cập nhật thông tin");
      }
    } catch (error) {
      console.error("Update profile error:", error);
      Alert.alert("Lỗi", error.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : "padding"}
        style={{ flex: 1 }}
        keyboardVerticalOffset={Platform.OS === "ios" ? 0 : 20}
      >
        <ScrollView
          contentContainerStyle={{ padding: 16, paddingBottom: 100 }}
          showsVerticalScrollIndicator={false}
          keyboardShouldPersistTaps="handled"
          keyboardDismissMode="interactive"
        >
          <Text style={{ fontSize: 24, fontWeight: "900", marginBottom: 8, color: colors.text }}>
            Chỉnh sửa thông tin
          </Text>
          <Text style={{ fontSize: 14, color: colors.subtext, marginBottom: 20 }}>
            Cập nhật thông tin cá nhân của bạn
          </Text>

          {/* Full Name - Not Editable */}
          <InputField
            label="Họ và tên (từ Google)"
            value={fullName}
            editable={false}
            placeholder="Tên từ Google"
            loading={loading}
          />

          {/* Email - Not Editable */}
          <InputField
            label="Email (từ Google)"
            value={user?.email || ""}
            editable={false}
            placeholder="Email từ Google"
            loading={loading}
          />

          {/* Editable fields */}
          <InputField
            label="MSSV"
            value={formData.studentId}
            onChangeText={(value) => handleInputChange("studentId", value)}
            placeholder="Nhập mã số sinh viên"
            keyboardType="numeric"
            loading={loading}
          />

          <InputField
            label="Số điện thoại"
            value={formData.phoneNumber}
            onChangeText={(value) => handleInputChange("phoneNumber", value)}
            placeholder="Nhập số điện thoại"
            keyboardType="phone-pad"
            loading={loading}
          />

          <InputField
            label="Khoa"
            value={formData.faculty}
            onChangeText={(value) => handleInputChange("faculty", value)}
            placeholder="VD: Khoa Khoa học và Kỹ thuật Máy tính"
            loading={loading}
          />

          <View style={{ marginTop: 20, marginBottom: 40 }}>
            <TouchableOpacity
              style={{
                backgroundColor: colors.primary,
                paddingVertical: 16,
                borderRadius: radii.md,
                marginBottom: 12,
              }}
              onPress={handleSave}
              disabled={loading}
            >
              {loading ? (
                <ActivityIndicator color="#fff" />
              ) : (
                <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800", fontSize: 16 }}>
                  Lưu thay đổi
                </Text>
              )}
            </TouchableOpacity>

            <TouchableOpacity
              style={{
                borderWidth: 1,
                borderColor: colors.subtext,
                paddingVertical: 16,
                borderRadius: radii.md,
              }}
              onPress={() => navigation.goBack()}
              disabled={loading}
            >
              <Text style={{ color: colors.subtext, textAlign: "center", fontWeight: "600", fontSize: 16 }}>
                Hủy
              </Text>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}