import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
} from "react-native";
import { colors, radii } from "../src/ui/theme";
import {
  getGroupById,
  joinGroup,
  leaveGroup,
  approveJoinRequest,
  rejectJoinRequest,
} from "../src/api/groupService";

export default function GroupInfoScreen({ route, navigation }) {
  const { groupId } = route.params;
  const [group, setGroup] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    loadGroupInfo();
  }, [groupId]);

  const loadGroupInfo = async () => {
    setLoading(true);
    try {
      const response = await getGroupById(groupId);
      if (response.code === 200) {
        setGroup(response.result);
      } else {
        Alert.alert("Lỗi", response.message || "Không thể tải thông tin nhóm");
      }
    } catch (error) {
      console.error("Load group error:", error);
      Alert.alert("Lỗi", "Có lỗi xảy ra khi tải thông tin nhóm");
    } finally {
      setLoading(false);
    }
  };

  const handleJoinGroup = async () => {
    Alert.alert(
      "Tham gia nhóm",
      "Bạn có muốn gửi yêu cầu tham gia nhóm này?",
      [
        { text: "Hủy", style: "cancel" },
        {
          text: "Tham gia",
          onPress: async () => {
            setActionLoading(true);
            try {
              const response = await joinGroup(groupId);
              if (response.code === 200) {
                Alert.alert("Thành công", "Đã gửi yêu cầu tham gia nhóm!");
                loadGroupInfo(); // Reload to see updated status
              } else {
                Alert.alert("Lỗi", response.message || "Không thể tham gia nhóm");
              }
            } catch (error) {
              console.error("Join group error:", error);
              Alert.alert("Lỗi", error.response?.data?.message || "Có lỗi xảy ra");
            } finally {
              setActionLoading(false);
            }
          },
        },
      ]
    );
  };

  const handleLeaveGroup = async () => {
    Alert.alert(
      "Rời nhóm",
      "Bạn có chắc muốn rời khỏi nhóm này?",
      [
        { text: "Hủy", style: "cancel" },
        {
          text: "Rời nhóm",
          style: "destructive",
          onPress: async () => {
            setActionLoading(true);
            try {
              const response = await leaveGroup(groupId);
              if (response.code === 200) {
                Alert.alert("Thành công", "Đã rời khỏi nhóm!", [
                  { text: "OK", onPress: () => navigation.goBack() },
                ]);
              } else {
                Alert.alert("Lỗi", response.message || "Không thể rời nhóm");
              }
            } catch (error) {
              console.error("Leave group error:", error);
              Alert.alert("Lỗi", error.response?.data?.message || "Có lỗi xảy ra");
            } finally {
              setActionLoading(false);
            }
          },
        },
      ]
    );
  };

  const handleApproveRequest = async (userId) => {
    setActionLoading(true);
    try {
      const response = await approveJoinRequest(groupId, userId);
      if (response.code === 200) {
        Alert.alert("Thành công", "Đã duyệt yêu cầu!");
        loadGroupInfo();
      } else {
        Alert.alert("Lỗi", response.message || "Không thể duyệt yêu cầu");
      }
    } catch (error) {
      console.error("Approve error:", error);
      Alert.alert("Lỗi", error.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      setActionLoading(false);
    }
  };

  const handleRejectRequest = async (userId) => {
    setActionLoading(true);
    try {
      const response = await rejectJoinRequest(groupId, userId);
      if (response.code === 200) {
        Alert.alert("Thành công", "Đã từ chối yêu cầu!");
        loadGroupInfo();
      } else {
        Alert.alert("Lỗi", response.message || "Không thể từ chối yêu cầu");
      }
    } catch (error) {
      console.error("Reject error:", error);
      Alert.alert("Lỗi", error.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  if (!group) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center", padding: 20 }}>
        <Text style={{ fontSize: 16, color: colors.subtext }}>
          Không tìm thấy thông tin nhóm
        </Text>
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          style={{ marginTop: 20, padding: 12, backgroundColor: colors.primary, borderRadius: radii.md }}
        >
          <Text style={{ color: "#fff", fontWeight: "600" }}>Quay lại</Text>
        </TouchableOpacity>
      </View>
    );
  }

  const InfoRow = ({ label, value }) => (
    <View style={{ marginBottom: 12 }}>
      <Text style={{ marginBottom: 6, color: "#666", fontWeight: "600", fontSize: 13 }}>
        {label}
      </Text>
      <View
        style={{
          borderWidth: 1,
          borderColor: "#E2E8F0",
          borderRadius: radii.md,
          paddingHorizontal: 12,
          paddingVertical: 12,
          backgroundColor: "#F7FAFC",
        }}
      >
        <Text style={{ fontSize: 15, color: colors.text }}>{value || "N/A"}</Text>
      </View>
    </View>
  );

  const MemberItem = ({ member }) => (
    <View
      style={{
        backgroundColor: "#fff",
        borderRadius: radii.md,
        padding: 12,
        marginBottom: 8,
        borderWidth: 1,
        borderColor: "#E2E8F0",
      }}
    >
      <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center" }}>
        <View style={{ flex: 1 }}>
          <Text style={{ fontWeight: "600", fontSize: 15 }}>
            {member.user?.firstName} {member.user?.lastName}
          </Text>
          <Text style={{ color: colors.subtext, fontSize: 13, marginTop: 2 }}>
            {member.user?.email}
          </Text>
          <Text style={{ color: colors.subtext, fontSize: 12, marginTop: 2 }}>
            Trạng thái: {member.status}
          </Text>
        </View>
        {member.status === "Đang chờ duyệt" && (
          <View style={{ flexDirection: "row" }}>
            <TouchableOpacity
              onPress={() => handleApproveRequest(member.user?.userId)}
              style={{ padding: 8, backgroundColor: "#10B981", borderRadius: radii.sm, marginRight: 8 }}
              disabled={actionLoading}
            >
              <Text style={{ color: "#fff", fontSize: 12, fontWeight: "600" }}>Duyệt</Text>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={() => handleRejectRequest(member.user?.userId)}
              style={{ padding: 8, backgroundColor: "#EF4444", borderRadius: radii.sm }}
              disabled={actionLoading}
            >
              <Text style={{ color: "#fff", fontSize: 12, fontWeight: "600" }}>Từ chối</Text>
            </TouchableOpacity>
          </View>
        )}
      </View>
    </View>
  );

  return (
    <ScrollView contentContainerStyle={{ padding: 16 }} showsVerticalScrollIndicator={false}>
      <Text style={{ fontSize: 22, fontWeight: "800", marginBottom: 16 }}>
        Thông tin nhóm
      </Text>

      <InfoRow label="Tên nhóm" value={group.name} />
      <InfoRow label="Mã môn học" value={group.course?.courseId} />
      <InfoRow label="Tên môn học" value={group.course?.name} />
      <InfoRow label="Mã lớp" value={group.groupClass} />
      <InfoRow label="Học kỳ" value={group.semester?.toString()} />
      <InfoRow label="Tên đề tài" value={group.topicName} />
      <InfoRow label="Số lượng thành viên" value={`${group.groupMembers?.length || 0}/${group.maxMembers}`} />

      <View style={{ marginBottom: 16 }}>
        <Text style={{ marginBottom: 6, color: "#666", fontWeight: "600", fontSize: 13 }}>
          Miêu tả đề tài
        </Text>
        <View
          style={{
            borderWidth: 1,
            borderColor: "#E2E8F0",
            borderRadius: radii.md,
            paddingHorizontal: 12,
            paddingVertical: 12,
            backgroundColor: "#F7FAFC",
            minHeight: 80,
          }}
        >
          <Text style={{ fontSize: 15, color: colors.text }}>
            {group.description || "Chưa có mô tả"}
          </Text>
        </View>
      </View>

      {/* Tags */}
      {group.groupTags && group.groupTags.length > 0 && (
        <View style={{ marginBottom: 16 }}>
          <Text style={{ fontSize: 16, fontWeight: "700", marginBottom: 8 }}>
            Tags
          </Text>
          <View style={{ flexDirection: "row", flexWrap: "wrap" }}>
            {group.groupTags.map((tag, index) => (
              <View
                key={index}
                style={{
                  backgroundColor: colors.primary + "20",
                  paddingHorizontal: 12,
                  paddingVertical: 6,
                  borderRadius: radii.sm,
                  marginRight: 8,
                  marginBottom: 8,
                }}
              >
                <Text style={{ color: colors.primary, fontSize: 13 }}>
                  {tag.name || tag}
                </Text>
              </View>
            ))}
          </View>
        </View>
      )}

      {/* Members */}
      <Text style={{ fontSize: 18, fontWeight: "800", marginTop: 8, marginBottom: 12 }}>
        Thành viên ({group.groupMembers?.length || 0})
      </Text>
      {group.groupMembers && group.groupMembers.length > 0 ? (
        group.groupMembers.map((member, index) => (
          <MemberItem key={index} member={member} />
        ))
      ) : (
        <Text style={{ color: colors.subtext, textAlign: "center", padding: 20 }}>
          Chưa có thành viên
        </Text>
      )}

      {/* Action Buttons */}
      <View style={{ marginTop: 20, marginBottom: 20 }}>
        <TouchableOpacity
          style={{
            backgroundColor: colors.primary,
            paddingVertical: 16,
            borderRadius: radii.md,
            marginBottom: 12,
          }}
          onPress={handleJoinGroup}
          disabled={actionLoading}
        >
          {actionLoading ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800", fontSize: 16 }}>
              Tham gia nhóm
            </Text>
          )}
        </TouchableOpacity>

        <TouchableOpacity
          style={{
            backgroundColor: "#EF4444",
            paddingVertical: 16,
            borderRadius: radii.md,
          }}
          onPress={handleLeaveGroup}
          disabled={actionLoading}
        >
          <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800", fontSize: 16 }}>
            Rời nhóm
          </Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}
