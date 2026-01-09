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

  // Phân loại notifications theo loại
  const invitations = notifications.filter(n => n.status === "PENDING_APPROVAL");
  const joinRequests = notifications.filter(n => n.status === "WAITING_APPROVAL");

  return (
    <Screen>
      <ScrollView
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={loadNotifications} />
        }
      >
        {/* Header với badge tổng số thông báo */}
        <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
          <Text style={{ fontSize: 22, fontWeight: "900" }}>
            Thông báo
          </Text>
          {notifications.length > 0 && (
            <View style={{
              backgroundColor: colors.primary,
              paddingHorizontal: 10,
              paddingVertical: 4,
              borderRadius: radii.md
            }}>
              <Text style={{ color: "#fff", fontSize: 12, fontWeight: "700" }}>
                {notifications.length}
              </Text>
            </View>
          )}
        </View>

        {notifications.length > 0 ? (
          <>
            {/* Section: Lời mời tham gia */}
            {invitations.length > 0 && (
              <View style={{ marginBottom: 20 }}>
                <View style={{
                  flexDirection: "row",
                  alignItems: "center",
                  marginBottom: 12,
                  paddingBottom: 8,
                  borderBottomWidth: 2,
                  borderBottomColor: colors.primary || "#3B82F6"
                }}>
                  <Text style={{ fontSize: 16, fontWeight: "800", color: colors.text }}>
                    📩 Lời mời tham gia
                  </Text>
                  <View style={{
                    backgroundColor: colors.primary || "#3B82F6",
                    paddingHorizontal: 8,
                    paddingVertical: 2,
                    borderRadius: radii.sm,
                    marginLeft: 8
                  }}>
                    <Text style={{ color: "#fff", fontSize: 11, fontWeight: "700" }}>
                      {invitations.length}
                    </Text>
                  </View>
                </View>
                {invitations.map((n, i) => (
                  <NotificationCard
                    key={`invite-${i}`}
                    notification={n}
                    onAction={loadNotifications}
                    currentUserId={userId}
                  />
                ))}
              </View>
            )}

            {/* Section: Yêu cầu vào nhóm */}
            {joinRequests.length > 0 && (
              <View style={{ marginBottom: 20 }}>
                <View style={{
                  flexDirection: "row",
                  alignItems: "center",
                  marginBottom: 12,
                  paddingBottom: 8,
                  borderBottomWidth: 2,
                  borderBottomColor: colors.pink || "#EC4899"
                }}>
                  <Text style={{ fontSize: 16, fontWeight: "800", color: colors.text }}>
                    👥 Yêu cầu vào nhóm
                  </Text>
                  <View style={{
                    backgroundColor: colors.pink || "#EC4899",
                    paddingHorizontal: 8,
                    paddingVertical: 2,
                    borderRadius: radii.sm,
                    marginLeft: 8
                  }}>
                    <Text style={{ color: "#fff", fontSize: 11, fontWeight: "700" }}>
                      {joinRequests.length}
                    </Text>
                  </View>
                </View>
                {joinRequests.map((n, i) => (
                  <NotificationCard
                    key={`request-${i}`}
                    notification={n}
                    onAction={loadNotifications}
                    currentUserId={userId}
                  />
                ))}
              </View>
            )}
          </>
        ) : (
          <View style={{ alignItems: "center", marginTop: 80 }}>
            <Text style={{ fontSize: 64, marginBottom: 16 }}>🔔</Text>
            <Text style={{ color: colors.text, textAlign: "center", fontSize: 18, fontWeight: "700" }}>
              Không có thông báo
            </Text>
            <Text style={{ color: colors.subtext, textAlign: "center", fontSize: 14, marginTop: 8, paddingHorizontal: 40 }}>
              Bạn sẽ nhận thông báo khi có lời mời hoặc yêu cầu tham gia nhóm
            </Text>
          </View>
        )}
      </ScrollView>
    </Screen>
  );
}