import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
} from "react-native";
import { colors, radii } from "../src/ui/theme";
import { inviteMemberByIdentifier } from "../src/api/groupService";

export default function InviteMemberScreen({ route, navigation }) {
  const { groupId } = route.params;
  const [identifier, setIdentifier] = useState("");
  const [loading, setLoading] = useState(false);

  const handleInvite = async () => {
    if (!identifier.trim()) {
      Alert.alert("Lỗi", "Vui lòng nhập MSSV hoặc Email");
      return;
    }

    setLoading(true);
    try {
      const response = await inviteMemberByIdentifier(groupId, identifier.trim());
      if (response.code === 200) {
        Alert.alert(
          "Thành công",
          "Đã gửi lời mời thành công!",
          [
            {
              text: "OK",
              onPress: () => navigation.goBack(),
            },
          ]
        );
      } else {
        Alert.alert("Lỗi", response.message || "Không thể gửi lời mời");
      }
    } catch (error) {
      console.error("Invite member error:", error);
      const errorMessage = error.response?.data?.message || "Có lỗi xảy ra";
      Alert.alert("Lỗi", errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : "height"}
      style={{ flex: 1, backgroundColor: colors.bg }}
    >
      <ScrollView
        contentContainerStyle={{ flexGrow: 1, padding: 20 }}
        keyboardShouldPersistTaps="handled"
      >
        <Text style={{ fontSize: 24, fontWeight: "800", marginBottom: 8 }}>
          Mời thành viên
        </Text>
        <Text style={{ fontSize: 14, color: colors.subtext, marginBottom: 24 }}>
          Nhập MSSV hoặc Email của người bạn muốn mời vào nhóm
        </Text>

        <View style={{ marginBottom: 20 }}>
          <Text style={{ marginBottom: 8, color: "#666", fontWeight: "600", fontSize: 14 }}>
            MSSV hoặc Email
          </Text>
          <TextInput
            style={{
              borderWidth: 1,
              borderColor: "#E2E8F0",
              borderRadius: radii.md,
              paddingHorizontal: 16,
              paddingVertical: 12,
              fontSize: 16,
              backgroundColor: "#fff",
            }}
            placeholder="Ví dụ: 2211234 hoặc user@hcmut.edu.vn"
            value={identifier}
            onChangeText={setIdentifier}
            autoCapitalize="none"
            autoCorrect={false}
            editable={!loading}
          />
          <Text style={{ marginTop: 6, fontSize: 12, color: colors.subtext, fontStyle: "italic" }}>
            Nhập MSSV (số) hoặc địa chỉ email của thành viên
          </Text>
        </View>

        <TouchableOpacity
          style={{
            backgroundColor: loading ? "#ccc" : colors.primary,
            paddingVertical: 16,
            borderRadius: radii.md,
            alignItems: "center",
            marginBottom: 12,
          }}
          onPress={handleInvite}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={{ color: "#fff", fontSize: 16, fontWeight: "800" }}>
              Gửi lời mời
            </Text>
          )}
        </TouchableOpacity>

        <TouchableOpacity
          style={{
            paddingVertical: 16,
            borderRadius: radii.md,
            alignItems: "center",
            borderWidth: 1,
            borderColor: colors.primary,
          }}
          onPress={() => navigation.goBack()}
          disabled={loading}
        >
          <Text style={{ color: colors.primary, fontSize: 16, fontWeight: "700" }}>
            Hủy
          </Text>
        </TouchableOpacity>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
