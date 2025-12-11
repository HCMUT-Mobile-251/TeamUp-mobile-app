import React, { useState, useEffect } from "react";
import { View, Text, TouchableOpacity, Alert, ActivityIndicator, ScrollView, RefreshControl } from "react-native";
import Screen from "../src/ui/Screen";
import Tag from "../src/components/Tag";
import { colors, radii, shadow } from "../src/ui/theme";
import { getNotificationsByUserId, deleteNotification } from "../src/api/notificationService";
import { acceptJoinRequest, rejectJoinRequest } from "../src/api/groupService";

function NotificationCard({ notification, onAction }) {
  const [actionLoading, setActionLoading] = useState(false);
  const { user, group, joinMessage, time, status } = notification;

  const handleAccept = async () => {
    Alert.alert(
      "Chấp thuận thành viên",
      `Bạn có muốn chấp thuận ${user?.fullName || "thành viên này"} vào nhóm?`,
      [
        { text: "Hủy", style: "cancel" },
        {
          text: "Chấp thuận",
          onPress: async () => {
            setActionLoading(true);
            try {
              const response = await acceptJoinRequest(group?.groupId, user?.userId);
              if (response.code === 200) {
                Alert.alert("Thành công", "Đã chấp thuận thành viên vào nhóm!");
                onAction();
              } else {
                Alert.alert("Lỗi", response.message || "Không thể chấp thuận");
              }
            } catch (error) {
              console.error("Accept error:", error);
              Alert.alert("Lỗi", error.response?.data?.message || "Có lỗi xảy ra");
            } finally {
              setActionLoading(false);
            }
          },
        },
      ]
    );
  };

  const handleReject = async () => {
    Alert.alert(
      "Từ chối thành viên",
      `Bạn có muốn từ chối ${user?.fullName || "thành viên này"}?`,
      [
        { text: "Hủy", style: "cancel" },
        {
          text: "Từ chối",
          style: "destructive",
          onPress: async () => {
            setActionLoading(true);
            try {
              const response = await rejectJoinRequest(group?.groupId, user?.userId);
              if (response.code === 200) {
                Alert.alert("Thành công", "Đã từ chối yêu cầu tham gia!");
                onAction();
              } else {
                Alert.alert("Lỗi", response.message || "Không thể từ chối");
              }
            } catch (error) {
              console.error("Reject error:", error);
              Alert.alert("Lỗi", error.response?.data?.message || "Có lỗi xảy ra");
            } finally {
              setActionLoading(false);
            }
          },
        },
      ]
    );
  };

  const handleDelete = async () => {
    Alert.alert(
      "Xóa thông báo",
      "Bạn có muốn xóa thông báo này?",
      [
        { text: "Hủy", style: "cancel" },
        {
          text: "Xóa",
          style: "destructive",
          onPress: async () => {
            setActionLoading(true);
            try {
              const response = await deleteNotification({
                firstId: user?.userId,
                secondId: group?.groupId,
              });
              if (response.code === 200) {
                Alert.alert("Thành công", "Đã xóa thông báo!");
                onAction();
              } else {
                Alert.alert("Lỗi", response.message || "Không thể xóa thông báo");
              }
            } catch (error) {
              console.error("Delete error:", error);
              Alert.alert("Lỗi", error.response?.data?.message || "Có lỗi xảy ra");
            } finally {
              setActionLoading(false);
            }
          },
        },
      ]
    );
  };

  const formatTime = (timestamp) => {
    if (!timestamp) return "";
    const date = new Date(timestamp);
    const now = new Date();
    const diffInMs = now - date;
    const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60));
    const diffInDays = Math.floor(diffInHours / 24);

    if (diffInHours < 1) return "Vừa xong";
    if (diffInHours < 24) return `${diffInHours} giờ trước`;
    if (diffInDays < 7) return `${diffInDays} ngày trước`;
    return date.toLocaleDateString("vi-VN");
  };

  return (
    <View
      style={[
        {
          backgroundColor: colors.card,
          borderRadius: radii.lg,
          padding: 16,
          marginBottom: 14,
        },
        shadow.card,
      ]}
    >
      <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "flex-start" }}>
        <View style={{ flex: 1 }}>
          <Text style={{ fontSize: 16, fontWeight: "800" }}>
            {group?.name || "Nhóm"}
          </Text>
          <Text style={{ fontSize: 12, color: colors.subtext, marginTop: 2 }}>
            {group?.topicName || ""}
          </Text>
        </View>
        <TouchableOpacity onPress={handleDelete} disabled={actionLoading}>
          <Text style={{ color: colors.subtext, fontSize: 12 }}>✕</Text>
        </TouchableOpacity>
      </View>

      {group?.groupTags && group.groupTags.length > 0 && (
        <View style={{ flexDirection: "row", flexWrap: "wrap", marginTop: 6 }}>
          {group.groupTags.slice(0, 4).map((tag) => (
            <Tag key={tag.tagId} label={tag.name} />
          ))}
        </View>
      )}

      <View
        style={{
          marginTop: 10,
          backgroundColor: colors.white,
          borderRadius: radii.md,
          padding: 12,
        }}
      >
        <Text style={{ fontWeight: "700", marginBottom: 4 }}>
          {user?.fullName || "Người dùng"}
        </Text>
        <Text style={{ color: colors.subtext, fontSize: 14 }}>
          {joinMessage || "Muốn tham gia nhóm"}
        </Text>
        <Text style={{ color: colors.subtext, fontSize: 12, marginTop: 4 }}>
          {formatTime(time)}
        </Text>
      </View>

      {status === "PENDING" && (
        <View
          style={{
            flexDirection: "row",
            justifyContent: "space-between",
            marginTop: 10,
          }}
        >
          <TouchableOpacity
            style={{
              backgroundColor: colors.pinkSoft,
              paddingHorizontal: 16,
              paddingVertical: 10,
              borderRadius: radii.md,
              flex: 1,
              marginRight: 8,
            }}
            onPress={handleReject}
            disabled={actionLoading}
          >
            {actionLoading ? (
              <ActivityIndicator size="small" color={colors.pink} />
            ) : (
              <Text style={{ color: colors.pink, fontWeight: "800", textAlign: "center" }}>
                Từ chối
              </Text>
            )}
          </TouchableOpacity>
          <TouchableOpacity
            style={{
              backgroundColor: colors.blueSoft,
              paddingHorizontal: 16,
              paddingVertical: 10,
              borderRadius: radii.md,
              flex: 1,
              marginLeft: 8,
            }}
            onPress={handleAccept}
            disabled={actionLoading}
          >
            {actionLoading ? (
              <ActivityIndicator size="small" color={colors.primary} />
            ) : (
              <Text style={{ color: colors.primary, fontWeight: "800", textAlign: "center" }}>
                Chấp thuận
              </Text>
            )}
          </TouchableOpacity>
        </View>
      )}
    </View>
  );
}

export default function NotificationsScreen({ navigation }) {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState(null);

  // TODO: Replace with actual userId from auth context
  const userId = "2211093"; // Hardcoded for now

  const loadNotifications = async (isRefreshing = false) => {
    if (!isRefreshing) setLoading(true);
    setError(null);

    try {
      const response = await getNotificationsByUserId(userId);
      if (response.code === 200) {
        // Filter only PENDING notifications
        const pending = (response.result || []).filter(
          (notif) => notif.status === "PENDING"
        );
        setNotifications(pending);
      } else {
        setError(response.message || "Không thể tải thông báo");
      }
    } catch (error) {
      console.error("Load notifications error:", error);
      setError(error.response?.data?.message || "Có lỗi xảy ra khi tải thông báo");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    loadNotifications();
  }, []);

  const handleRefresh = () => {
    setRefreshing(true);
    loadNotifications(true);
  };

  const handleAction = () => {
    // Reload notifications after any action
    loadNotifications();
  };

  if (loading) {
    return (
      <Screen>
        <Text style={{ fontSize: 22, fontWeight: "900", marginBottom: 12 }}>
          Thông báo
        </Text>
        <View style={{ padding: 20, alignItems: "center" }}>
          <ActivityIndicator size="large" color={colors.primary} />
        </View>
      </Screen>
    );
  }

  if (error) {
    return (
      <Screen>
        <Text style={{ fontSize: 22, fontWeight: "900", marginBottom: 12 }}>
          Thông báo
        </Text>
        <View style={{ padding: 24, alignItems: "center" }}>
          <Text style={{ color: colors.subtext, fontSize: 16, marginBottom: 12 }}>
            {error}
          </Text>
          <TouchableOpacity
            onPress={() => loadNotifications()}
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
      </Screen>
    );
  }

  return (
    <Screen>
      <ScrollView
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={handleRefresh} />
        }
      >
        <Text style={{ fontSize: 22, fontWeight: "900", marginBottom: 12 }}>
          Thông báo
        </Text>
        {notifications.length > 0 ? (
          notifications.map((notification, index) => (
            <NotificationCard
              key={`${notification.user?.userId}-${notification.group?.groupId}-${index}`}
              notification={notification}
              onAction={handleAction}
            />
          ))
        ) : (
          <View style={{ padding: 24, alignItems: "center" }}>
            <Text style={{ color: colors.subtext, fontSize: 16 }}>
              Chưa có thông báo nào
            </Text>
          </View>
        )}
      </ScrollView>
    </Screen>
  );
}
