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

/* ================================================= */
/* ================= CARD ========================== */
/* ================================================= */

function NotificationCard({ notification, onAction }) {
  const { user, group, status, time } = notification;
  const { userId: currentUserId } = useContext(AuthContext);
  const [loading, setLoading] = useState(false);

  const isPending = status === "Chờ được chấp nhận!";

  // 1️⃣ Bạn được mời
  const isInvite =
    isPending &&
    user?.userId === currentUserId &&
    group?.leaderId !== currentUserId;

  // 2️⃣ Bạn gửi request → đang chờ
  const isMyJoinRequest =
    isPending &&
    user?.userId === currentUserId &&
    group?.leaderId === currentUserId;

  // 3️⃣ Leader duyệt request
  const isLeaderApprove =
    isPending &&
    user?.userId !== currentUserId;

  const formatTime = (t) => {
    if (!t) return "";
    const d = new Date(t);
    const diff = Math.floor((Date.now() - d) / 3600000);
    if (diff < 1) return "Vừa xong";
    if (diff < 24) return `${diff} giờ trước`;
    return d.toLocaleDateString("vi-VN");
  };

  /* ============ ACTIONS ============ */

  const accept = async () => {
    setLoading(true);
    await acceptJoinRequest(group.groupId, user.userId);
    onAction();
  };

  const reject = async (msg) => {
    Alert.alert(msg, "Bạn chắc chắn chứ?", [
      { text: "Hủy", style: "cancel" },
      {
        text: "Xác nhận",
        style: "destructive",
        onPress: async () => {
          setLoading(true);
          await rejectJoinRequest(group.groupId, user.userId);
          onAction();
        },
      },
    ]);
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
      <Text style={{ fontWeight: "800", fontSize: 16 }}>
        {group?.name}
      </Text>

      <Text style={{ marginTop: 6, color: colors.subtext }}>
        {isInvite && `Bạn được mời tham gia nhóm`}
        {isMyJoinRequest && `Yêu cầu tham gia nhóm`}
        {isLeaderApprove &&
          `${user?.fullName} muốn tham gia nhóm`}
      </Text>

      <Text style={{ fontSize: 12, color: colors.subtext, marginTop: 4 }}>
        {formatTime(time)}
      </Text>

      {/* ================= ACTION UI ================= */}

      {loading && (
        <ActivityIndicator
          style={{ marginTop: 10 }}
          color={colors.primary}
        />
      )}

      {/* INVITE */}
      {isInvite && !loading && (
        <View style={{ flexDirection: "row", marginTop: 10 }}>
          <Button text="Từ chối" onPress={() => reject("Từ chối lời mời")} />
          <Button
            text="Tham gia"
            primary
            onPress={accept}
          />
        </View>
      )}

      {/* MY REQUEST */}
      {isMyJoinRequest && !loading && (
        <View style={{ marginTop: 10 }}>
          <Button
            text="Hủy yêu cầu"
            danger
            onPress={() => reject("Hủy yêu cầu tham gia")}
          />
        </View>
      )}

      {/* LEADER */}
      {isLeaderApprove && !loading && (
        <View style={{ flexDirection: "row", marginTop: 10 }}>
          <Button text="Từ chối" onPress={() => reject("Từ chối yêu cầu")} />
          <Button
            text="Chấp thuận"
            primary
            onPress={accept}
          />
        </View>
      )}
    </View>
  );
}

/* ================= BUTTON ================= */

function Button({ text, onPress, primary, danger }) {
  return (
    <TouchableOpacity
      onPress={onPress}
      style={{
        flex: 1,
        marginHorizontal: 4,
        padding: 10,
        borderRadius: radii.md,
        backgroundColor: primary
          ? colors.primary
          : danger
          ? colors.pinkSoft
          : colors.blueSoft,
      }}
    >
      <Text
        style={{
          textAlign: "center",
          fontWeight: "800",
          color: primary ? "#fff" : colors.text,
        }}
      >
        {text}
      </Text>
    </TouchableOpacity>
  );
}

/* ================================================= */
/* ================= SCREEN ======================== */
/* ================================================= */

export default function NotificationsScreen({ navigation }) {
  const { userId } = useContext(AuthContext);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    const res = await getNotificationsByUserId(userId);
    setData(
      (res.result || []).filter(
        (n) => n.status === "Chờ được chấp nhận!"
      )
    );
    setLoading(false);
  };

  useEffect(() => {
    load();
  }, []);

  const onAction = async () => {
    await load();
    navigation.navigate("Home", { refresh: true });
  };

  if (loading) {
    return (
      <Screen>
        <ActivityIndicator color={colors.primary} />
      </Screen>
    );
  }

  return (
    <Screen>
      <ScrollView
        refreshControl={
          <RefreshControl refreshing={loading} onRefresh={load} />
        }
      >
        <Text style={{ fontSize: 22, fontWeight: "900", marginBottom: 12 }}>
          Thông báo
        </Text>

        {data.length > 0 ? (
          data.map((n, i) => (
            <NotificationCard
              key={i}
              notification={n}
              onAction={onAction}
            />
          ))
        ) : (
          <Text style={{ textAlign: "center", color: colors.subtext }}>
            Không có thông báo
          </Text>
        )}
      </ScrollView>
    </Screen>
  );
}
