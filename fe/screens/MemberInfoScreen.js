import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  ScrollView,
  SafeAreaView,
  ActivityIndicator,
  TouchableOpacity,
} from "react-native";
import { colors, radii, shadow } from "../src/ui/theme";
import { getUserById } from "../src/api/userService";
import Tag from "../src/components/Tag";

export default function MemberInfoScreen({ route, navigation }) {
  const { userId } = route.params;
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadUserData();
  }, [userId]);

  const loadUserData = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await getUserById(userId);
      console.log("[MemberInfoScreen] User Data Response:", response);

      if (response?.code === 200 && response?.result) {
        setUserData(response.result);
      } else if (response?.message) {
        setError(response.message);
      } else {
        setError("Không thể tải thông tin thành viên");
      }
    } catch (error) {
      console.error("Load user error:", error);

      if (error.response?.status === 401) {
        setError("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
      } else if (error.response?.status === 403) {
        setError("Bạn không có quyền xem thông tin này.");
      } else if (error.response?.status === 404) {
        setError("Không tìm thấy người dùng.");
      } else if (error.response?.data?.message) {
        setError(error.response.data.message);
      } else if (error.message === "Network Error") {
        setError("Không thể kết nối tới máy chủ. Kiểm tra kết nối internet.");
      } else {
        setError("Có lỗi xảy ra khi tải dữ liệu.");
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
        <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
          <ActivityIndicator size="large" color={colors.primary} />
          <Text style={{ marginTop: 12, color: colors.subtext }}>
            Đang tải thông tin...
          </Text>
        </View>
      </SafeAreaView>
    );
  }

  if (error) {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
        <View style={{ flex: 1, justifyContent: "center", alignItems: "center", padding: 24 }}>
          <Text style={{ color: colors.subtext, fontSize: 16, marginBottom: 12, textAlign: "center" }}>
            {error}
          </Text>
          <TouchableOpacity
            onPress={() => loadUserData()}
            style={{
              backgroundColor: colors.primary,
              paddingHorizontal: 20,
              paddingVertical: 10,
              borderRadius: radii.md,
            }}
          >
            <Text style={{ color: colors.white, fontWeight: "700" }}>Thử lại</Text>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
      <ScrollView contentContainerStyle={{ padding: 16 }}>
        <Text style={{ fontSize: 24, fontWeight: "900", color: colors.text, marginBottom: 16 }}>
          Thông tin thành viên
        </Text>

        {/* Card thông tin liên lạc */}
        <View
          style={[
            {
              backgroundColor: colors.white,
              borderRadius: radii.lg,
              padding: 20,
              marginBottom: 16,
            },
            shadow.card,
          ]}
        >
          <Text style={{ fontSize: 18, fontWeight: "800", color: colors.text, marginBottom: 16 }}>
            Thông tin liên lạc
          </Text>
          <Row
            label="Họ và tên"
            value={`${userData?.firstName || ""} ${userData?.lastName || ""}`.trim() || "Chưa cập nhật"}
          />
          <Row label="Email" value={userData?.email || "Chưa cập nhật"} />
          <Row label="Số điện thoại" value={userData?.phoneNumber || "Chưa cập nhật"} />
          <Row label="MSSV" value={userData?.studentId || "Chưa cập nhật"} />
          <Row label="Khoa" value={userData?.faculty || "Chưa cập nhật"} />
        </View>

        {/* Tags quan tâm */}
        <View style={{ marginBottom: 16 }}>
          <Text
            style={{
              fontWeight: "800",
              fontSize: 18,
              color: colors.text,
              marginBottom: 8,
            }}
          >
            Tags quan tâm
          </Text>
          <View
            style={{
              flexDirection: "row",
              flexWrap: "wrap",
              backgroundColor: colors.white,
              borderRadius: radii.lg,
              padding: 16,
              ...shadow.card,
            }}
          >
            {userData?.userTags && userData.userTags.length > 0 ? (
              userData.userTags.map((userTag) => (
                <Tag key={userTag.tag?.tagId || userTag.tagId} label={userTag.tag?.name || userTag.name} />
              ))
            ) : (
              <Text style={{ color: colors.subtext, fontSize: 14 }}>
                Chưa có tags quan tâm
              </Text>
            )}
          </View>
        </View>

        {/* Stats */}
        <View
          style={{
            flexDirection: "row",
            justifyContent: "space-around",
            backgroundColor: colors.white,
            borderRadius: radii.lg,
            padding: 20,
            ...shadow.card,
          }}
        >
          <View style={{ alignItems: "center" }}>
            <Text style={{ fontSize: 24, fontWeight: "800", color: colors.primary }}>
              {userData?.groups?.length || 0}
            </Text>
            <Text style={{ color: colors.subtext, marginTop: 4 }}>Nhóm tham gia</Text>
          </View>
          <View style={{ width: 1, backgroundColor: colors.subtext, opacity: 0.2 }} />
          <View style={{ alignItems: "center" }}>
            <Text style={{ fontSize: 24, fontWeight: "800", color: colors.pink }}>
              {userData?.userTags?.length || 0}
            </Text>
            <Text style={{ color: colors.subtext, marginTop: 4 }}>Tags quan tâm</Text>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

/* Component nhỏ để render từng dòng */
const Row = ({ label, value }) => (
  <View style={{ marginBottom: 12 }}>
    <Text style={{ fontWeight: "700", color: colors.subtext }}>{label}</Text>
    <Text style={{ color: colors.text, marginTop: 2 }}>{value}</Text>
  </View>
);
