import React, { useState, useContext } from "react";
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
import { sendJoinRequest } from "../src/api/groupService";
import { AuthContext } from "../App";

export default function JoinGroupScreen({ route, navigation }) {
  const { groupId } = route?.params || {};
  const { userId } = useContext(AuthContext);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const handleSendRequest = async () => {
    if (!groupId) {
      Alert.alert("Lỗi", "Không tìm thấy thông tin nhóm");
      return;
    }

    if (!message.trim()) {
      Alert.alert("Lỗi", "Vui lòng nhập lời chào hoặc lý do tham gia nhóm");
      return;
    }

    setLoading(true);
    try {
      const response = await sendJoinRequest(groupId, {
        userId: userId,
        message: message.trim(),
      });

      if (response.code === 200) {
        Alert.alert(
          "Thành công",
          "Yêu cầu tham gia nhóm đã được gửi!",
          [
            {
              text: "OK",
              onPress: () => navigation.goBack(),
            },
          ]
        );
      } else {
        Alert.alert("Lỗi", response.message || "Không thể gửi yêu cầu");
      }
    } catch (error) {
      console.error("Send join request error:", error);
      Alert.alert("Lỗi", "Đã xảy ra lỗi khi gửi yêu cầu. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView
      contentContainerStyle={{ padding: 16 }}
      style={{ flex: 1, backgroundColor: colors.bg }}
    >
      <Text style={{ fontSize: 22, fontWeight: "800", marginBottom: 8 }}>
        Tham Gia nhóm
      </Text>
      <Text style={{ fontSize: 14, color: colors.subtext, marginBottom: 20 }}>
        Gửi yêu cầu tham gia nhóm để leader xét duyệt
      </Text>

      <Text
        style={{
          fontSize: 16,
          fontWeight: "800",
          marginBottom: 8,
        }}
      >
        Lời chào & Lý do tham gia
      </Text>
      <Text style={{ fontSize: 13, color: colors.subtext, marginBottom: 8 }}>
        Giới thiệu bản thân và nêu lý do bạn muốn tham gia nhóm này
      </Text>
      <View
        style={{
          borderWidth: 1,
          borderColor: "#E2E8F0",
          borderRadius: radii.lg,
          paddingHorizontal: 12,
          paddingVertical: 12,
          minHeight: 120,
          backgroundColor: colors.white,
          marginBottom: 20,
        }}
      >
        <TextInput
          placeholder="Ví dụ: Xin chào, mình là sinh viên năm 3 chuyên ngành AI. Mình rất quan tâm đến đề tài này và muốn đóng góp vào nhóm..."
          multiline
          numberOfLines={6}
          value={message}
          onChangeText={setMessage}
          style={{ fontSize: 15, textAlignVertical: "top" }}
        />
      </View>

      <TouchableOpacity
        onPress={handleSendRequest}
        disabled={loading}
        style={{
          marginTop: 10,
          backgroundColor: loading ? colors.subtext : colors.primary,
          paddingVertical: 16,
          borderRadius: radii.lg,
          flexDirection: "row",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        {loading ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800", fontSize: 16 }}>
            Gửi yêu cầu
          </Text>
        )}
      </TouchableOpacity>

      <TouchableOpacity
        onPress={() => navigation.goBack()}
        disabled={loading}
        style={{
          marginTop: 12,
          paddingVertical: 16,
          borderRadius: radii.lg,
        }}
      >
        <Text style={{ color: colors.subtext, textAlign: "center", fontWeight: "600" }}>
          Hủy
        </Text>
      </TouchableOpacity>
    </ScrollView>
  );
}
