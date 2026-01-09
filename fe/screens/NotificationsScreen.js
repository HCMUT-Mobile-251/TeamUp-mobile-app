import React, { useState, useEffect, useContext } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
  ScrollView,
  RefreshControl,
} from "react-native";
import Screen from "../src/ui/Screen";
import { colors, radii, shadow } from "../src/ui/theme";
import {
  getNotificationsByUserId,
  deleteNotification,
} from "../src/api/notificationService";
import {
  acceptJoinRequest,
  rejectJoinRequest,
} from "../src/api/groupService";
import { AuthContext } from "../App";

/* ====================== CARD ====================== */

function NotificationCard({ notification, onAction, currentUserId }) {  // Thêm currentUserId prop
  const [actionLoading, setActionLoading] = useState(false);

  const { user, group, time, status, id } = notification;

  const isInvite = status === "PENDING_APPROVAL";   // user được mời
  const isRequest = status === "WAITING_APPROVAL";  // leader nhận request

  // userId của người cần được accept/reject
  // - PENDING_APPROVAL: currentUserId (user được mời)
  // - WAITING_APPROVAL: id.firstId (user gửi request)
  const targetUserId = isInvite ? currentUserId : id?.firstId;

  let title = "Thông báo";
  let content = "";

  if (isInvite) {
    title = "Lời mời tham gia nhóm";
    content = `Bạn được mời tham gia nhóm ${group?.name}`;
  }

  if (isRequest) {
    title = user?.fullName || "Người dùng";
    content = `Muốn tham gia nhóm ${group?.name}`;
  }

  const formatTime = (t) => {
    if (!t) return "";
    const d = new Date(t);
    return d.toLocaleDateString("vi-VN");
  };

  /* ========== ACCEPT ========== */
  const handleAccept = async () => {
    Alert.alert(
      "Xác nhận",
      isInvite
        ? `Tham gia nhóm ${group?.name}?`
        : `Chấp thuận ${user?.fullName} vào nhóm?`,
      [
        { text: "Hủy", style: "cancel" },
        {
          text: "Đồng ý",
          onPress: async () => {
            setActionLoading(true);
            try {
              const res = await acceptJoinRequest(group.groupId, targetUserId);

              if (res?.data?.code === 200) {
                Alert.alert("Thành công", "Đã xử lý thành công");
                onAction();
              } else {
                Alert.alert("Lỗi", res?.data?.message || "Không thể xử lý");
              }
            } catch (error) {
              console.error("Accept error:", error);
              Alert.alert(
                "Lỗi",
                error?.response?.data?.message || "Có lỗi xảy ra"
              );
            } finally {
              setActionLoading(false);
            }
          },
        },
      ]
    );
  };

  /* ========== REJECT ========== */
  const handleReject = async () => {
    Alert.alert(
      "Xác nhận",
      "Bạn có chắc muốn từ chối?",
      [
        { text: "Hủy", style: "cancel" },
        {
          text: "Từ chối",
          style: "destructive",
          onPress: async () => {
            setActionLoading(true);
            try {
              const res = await rejectJoinRequest(group.groupId, targetUserId);

              if (res?.data?.code === 200) {
                Alert.alert("Thành công", "Đã từ chối");
                onAction();
              } else {
                Alert.alert("Lỗi", res?.data?.message || "Không thể từ chối");
              }
            } catch (error) {
              console.error("Reject error:", error);
              Alert.alert(
                "Lỗi",
                error?.response?.data?.message || "Có lỗi xảy ra"
              );
            } finally {
              setActionLoading(false);
            }
          },
        },
      ]
    );
  };

  /* ========== DELETE NOTI ========== */
  const handleDelete = async () => {
    try {
      // Sử dụng id.firstId và id.secondId từ notification
      await deleteNotification({
        firstId: id?.firstId || targetUserId,
        secondId: id?.secondId || group?.groupId,
      });
      onAction();
    } catch (error) {
      console.error("Delete notification error:", error);
    }
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
      <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
        <Text style={{ fontSize: 16, fontWeight: "800" }}>
          {group?.name || "Nhóm"}
        </Text>
        <TouchableOpacity onPress={handleDelete}>
          <Text style={{ color: colors.subtext }}>✕</Text>
        </TouchableOpacity>
      </View>

      <Text style={{ marginTop: 8, fontWeight: "700" }}>{title}</Text>
      <Text style={{ color: colors.subtext, marginTop: 4 }}>{content}</Text>
      <Text style={{ color: colors.subtext, fontSize: 12, marginTop: 4 }}>
        {formatTime(time)}
      </Text>

      {(isInvite || isRequest) && (
        <View style={{ flexDirection: "row", marginTop: 12 }}>
          <TouchableOpacity
            style={{
              flex: 1,
              backgroundColor: colors.pinkSoft,
              padding: 10,
              borderRadius: radii.md,
              marginRight: 8,
            }}
            onPress={handleReject}
            disabled={actionLoading}
          >
            {actionLoading ? (
              <ActivityIndicator size="small" color={colors.pink} />
            ) : (
              <Text style={{ color: colors.pink, textAlign: "center", fontWeight: "800" }}>
                Từ chối
              </Text>
            )}
          </TouchableOpacity>

          <TouchableOpacity
            style={{
              flex: 1,
              backgroundColor: colors.blueSoft,
              padding: 10,
              borderRadius: radii.md,
            }}
            onPress={handleAccept}
            disabled={actionLoading}
          >
            {actionLoading ? (
              <ActivityIndicator size="small" color={colors.primary} />
            ) : (
              <Text style={{ color: colors.primary, textAlign: "center", fontWeight: "800" }}>
                Chấp thuận
              </Text>
            )}
          </TouchableOpacity>
        </View>
      )}
    </View>
  );
}

/* ====================== SCREEN ====================== */

export default function NotificationsScreen() {
  const { userId } = useContext(AuthContext);

  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const loadNotifications = async () => {
    try {
      const res = await getNotificationsByUserId(userId);
      if (res.code === 200) {
        setNotifications(
          (res.result || []).filter(
            (n) =>
              n.status === "WAITING_APPROVAL" ||
              n.status === "PENDING_APPROVAL"
          )
        );
      }
    } catch (error) {
      console.error("Load notifications error:", error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    if (userId) {
      loadNotifications();
    }
  }, [userId]);

  if (loading) {
    return (
      <Screen>
        <ActivityIndicator size="large" color={colors.primary} />
      </Screen>
    );
  }

  return (
    <Screen>
      <ScrollView
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={loadNotifications} />
        }
      >
        <Text style={{ fontSize: 22, fontWeight: "900", marginBottom: 12 }}>
          Thông báo
        </Text>

        {notifications.length > 0 ? (
          notifications.map((n, i) => (
            <NotificationCard
              key={i}
              notification={n}
              onAction={loadNotifications}
              currentUserId={userId}  // Truyền userId từ context
            />
          ))
        ) : (
          <Text style={{ color: colors.subtext, textAlign: "center", marginTop: 40 }}>
            Không có thông báo
          </Text>
        )}
      </ScrollView>
    </Screen>
  );
}