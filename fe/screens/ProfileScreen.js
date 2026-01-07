import React, { useContext, useState, useEffect } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  SafeAreaView,
  ActivityIndicator,
  Alert,
  RefreshControl,
} from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import { useNavigation, useFocusEffect } from "@react-navigation/native";
import Tag from "../src/components/Tag";
import { AuthContext } from "../App";
import { colors, radii, shadow } from "../src/ui/theme";
import { getUserById } from "../src/api/userService";

export default function ProfileScreen() {
  const { userId, signOut, resetOnboarding } = useContext(AuthContext);
  const navigation = useNavigation();

  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState(null);

  const loadUserData = async (isRefreshing = false) => {
    console.log("[ProfileScreen] loadUserData called, userId:", userId);
    
    if (!userId) {
      setError("Không có userId. Vui lòng đăng nhập lại.");
      setLoading(false);
      return;
    }
    
    if (!isRefreshing) setLoading(true);
    setError(null);

    try {
      const response = await getUserById(userId);
      console.log("[ProfileScreen] User Data Response:", response);
      
      if (response?.code === 200 && response?.result) {
        setUserData(response.result);
      } else if (response?.message) {
        setError(response.message);
      } else {
        setError("Không thể tải thông tin người dùng");
      }
    } catch (error) {
      console.error("Load user error:", error);
      
      // Xử lý các loại lỗi khác nhau
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
      setRefreshing(false);
    }
  };

  useEffect(() => {
    loadUserData();
  }, [userId]);

  // Reload data when screen comes into focus (e.g., returning from SelectTags)
  useFocusEffect(
    React.useCallback(() => {
      if (userId) {
        loadUserData(true);
      }
    }, [userId])
  );

  const handleRefresh = () => {
    setRefreshing(true);
    loadUserData(true);
  };

  const handleViewOnboarding = () => {
    // Navigate đến Onboarding1 để user xem lại giới thiệu
    // Không cần reset flag vì sau khi xem xong sẽ quay về app
    navigation.navigate("Onboarding1");
  };

  const handleEditProfile = () => {
    Alert.alert(
      "Chức năng đang phát triển",
      "Tính năng chỉnh sửa thông tin cá nhân sẽ được thêm trong phiên bản tiếp theo!"
    );
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
      <ScrollView
        contentContainerStyle={{ padding: 16 }}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={handleRefresh} />
        }
      >
        <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center" }}>
          <Text style={{ fontSize: 24, fontWeight: "900", color: colors.text }}>
            Thông tin cá nhân
          </Text>
          <TouchableOpacity onPress={handleEditProfile}>
            <Text style={{ color: colors.primary, fontWeight: "600" }}>Chỉnh sửa</Text>
          </TouchableOpacity>
        </View>

        {/* Card thông tin */}
        <View
          style={[
            {
              backgroundColor: colors.white,
              borderRadius: radii.lg,
              padding: 20,
              marginTop: 16,
            },
            shadow.card,
          ]}
        >
          <Row label="Tên" value={userData?.fullName || "Chưa cập nhật"} />
          <Row label="MSSV" value={userData?.studentId || "Chưa cập nhật"} />
          <Row label="Khoa" value={userData?.faculty || "Chưa cập nhật"} />
          <Row label="Số điện thoại" value={userData?.phoneNumber || "Chưa cập nhật"} />
          <Row label="Email" value={userData?.email || "Chưa cập nhật"} />
        </View>

        {/* Tags quan tâm */}
        <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginTop: 24, marginBottom: 8 }}>
          <Text
            style={{
              fontWeight: "800",
              fontSize: 18,
              color: colors.text,
            }}
          >
            Tags quan tâm
          </Text>
          <TouchableOpacity onPress={() => navigation.navigate("SelectTags")}>
            <Text style={{ color: colors.primary, fontWeight: "600" }}>Chỉnh sửa</Text>
          </TouchableOpacity>
        </View>
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

        {/* Stats */}
        <View
          style={{
            flexDirection: "row",
            justifyContent: "space-around",
            backgroundColor: colors.white,
            borderRadius: radii.lg,
            padding: 20,
            marginTop: 16,
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

        {/* Nút xem onboarding */}
        <TouchableOpacity
          activeOpacity={0.9}
          onPress={handleViewOnboarding}
          style={{ marginTop: 32, borderRadius: radii.lg, ...shadow.card }}
        >
          <LinearGradient
            colors={[colors.pink, "#FF6BA9"]}
            start={{ x: 0, y: 0 }}
            end={{ x: 1, y: 1 }}
            style={{
              borderRadius: radii.lg,
              paddingVertical: 16,
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Text
              style={{
                color: colors.white,
                fontWeight: "800",
                fontSize: 16,
              }}
            >
              Xem giới thiệu app
            </Text>
          </LinearGradient>
        </TouchableOpacity>

        {/* Nút đăng xuất */}
        <TouchableOpacity
          activeOpacity={0.9}
          onPress={signOut}
          style={{ marginTop: 16, marginBottom: 20, borderRadius: radii.lg, ...shadow.card }}
        >
          <LinearGradient
            colors={[colors.primary, colors.primary2]}
            start={{ x: 0, y: 0 }}
            end={{ x: 1, y: 1 }}
            style={{
              borderRadius: radii.lg,
              paddingVertical: 16,
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Text
              style={{
                color: colors.white,
                fontWeight: "800",
                fontSize: 16,
              }}
            >
              Đăng xuất
            </Text>
          </LinearGradient>
        </TouchableOpacity>
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
