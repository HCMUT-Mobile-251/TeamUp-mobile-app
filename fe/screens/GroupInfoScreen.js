import React, { useState, useEffect, useContext } from "react";
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
  sendJoinRequest,
  leaveGroup,
  acceptJoinRequest,
  rejectJoinRequest,
} from "../src/api/groupService";
import { AuthContext } from "../App";

export default function GroupInfoScreen({ route, navigation }) {
  const { groupId } = route.params;
  const { userId } = useContext(AuthContext);
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
    if (!userId) {
      Alert.alert("Lỗi", "Không tìm thấy thông tin người dùng");
      return;
    }

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
              const response = await sendJoinRequest(groupId, {
                userId: userId,
                message: "Xin chào, tôi muốn tham gia nhóm này!"
              });
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
    if (!userId) {
      Alert.alert("Lỗi", "Không tìm thấy thông tin người dùng");
      return;
    }

    // Check if user is leader
    if (isLeader) {
      // Count JOINED members excluding leader (if leader is also in groupMembers)
      const acceptedMembers = group.groupMembers?.filter(
        m => m.status === "JOINED" && m.user?.userId !== userId
      ) || [];

      console.log("Leader checking leave - JOINED members (excluding leader):", acceptedMembers.length);
      console.log("Total groupMembers:", group.groupMembers?.length);

      if (acceptedMembers.length > 0) {
        Alert.alert(
          "Không thể rời nhóm",
          "Bạn là trưởng nhóm. Vui lòng chuyển quyền trưởng nhóm cho thành viên khác trước khi rời nhóm."
        );
        return;
      } else {
        // Leader is the only member, confirm delete group
        Alert.alert(
          "Xóa nhóm",
          "Bạn là thành viên duy nhất trong nhóm. Rời nhóm sẽ xóa nhóm này. Bạn có chắc chắn?",
          [
            { text: "Hủy", style: "cancel" },
            {
              text: "Xóa nhóm",
              style: "destructive",
              onPress: async () => {
                setActionLoading(true);
                try {
                  console.log("Attempting to delete group:", groupId, "by leader:", userId);
                  const response = await leaveGroup(groupId, userId);
                  console.log("Delete group response:", response);
                  if (response.code === 200) {
                    Alert.alert("Thành công", "Đã xóa nhóm!", [
                      {
                        text: "OK",
                        onPress: () => navigation.navigate("Tabs", {
                          screen: "Home",
                          params: { refresh: true }
                        })
                      },
                    ]);
                  } else {
                    Alert.alert("Lỗi", response.message || "Không thể xóa nhóm");
                  }
                } catch (error) {
                  console.error("Delete group error:", error);
                  Alert.alert("Lỗi", error.response?.data?.message || "Có lỗi xảy ra");
                } finally {
                  setActionLoading(false);
                }
              },
            },
          ]
        );
      }
      return;
    }

    // Normal member leaving
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
              const response = await leaveGroup(groupId, userId);
              if (response.code === 200) {
                Alert.alert("Thành công", "Đã rời khỏi nhóm!", [
                  {
                    text: "OK",
                    onPress: () => navigation.navigate("Tabs", {
                      screen: "Home",
                      params: { refresh: true }
                    })
                  },
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
      const response = await acceptJoinRequest(groupId, userId);
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

  // Check if user is in the group
  const userMember = group.groupMembers?.find(m => m.user?.userId === userId);
  const isUserInGroup = !!userMember && userMember.status === "JOINED";
  const isLeader = group.leaderId?.userId === userId;

  // Count members: JOINED members + leader (if leader is not in groupMembers)
  const joinedMembers = group.groupMembers?.filter(m => m.status === "JOINED") || [];
  const leaderInMembers = joinedMembers.some(m => m.user?.userId === group.leaderId?.userId);
  const memberCount = joinedMembers.length + (leaderInMembers ? 0 : 1);

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

  const MemberItem = ({ member }) => {
    // Translate status to Vietnamese
    const getStatusText = (status) => {
      switch (status) {
        case "PENDING":
          return "Đang chờ duyệt";
        case "ACCEPTED":
          return "Đã được chấp nhận";
        case "REJECTED":
          return "Đã bị từ chối";
        case "JOINED":
          return "Đã tham gia";
        default:
          return status || "N/A";
      }
    };

    const handleMemberPress = () => {
      // Only navigate if member is JOINED (accepted member)
      if (member.status === "JOINED") {
        navigation.navigate("MemberInfo", { userId: member.user?.userId });
      }
    };

    return (
      <TouchableOpacity
        onPress={handleMemberPress}
        disabled={member.status !== "JOINED"}
        activeOpacity={member.status === "JOINED" ? 0.7 : 1}
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
              Trạng thái: {getStatusText(member.status)}
            </Text>
            {member.status === "JOINED" && (
              <Text style={{ color: colors.primary, fontSize: 11, marginTop: 2, fontStyle: "italic" }}>
                Nhấn để xem thông tin liên lạc
              </Text>
            )}
          </View>
          {member.status === "PENDING" && isLeader && (
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
      </TouchableOpacity>
    );
  };

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
      <InfoRow label="Số lượng thành viên" value={`${memberCount}/${group.maxMembers}`} />

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
            {group.groupTags.map((groupTag, index) => {
              // Handle both nested {tag: {...}} and direct tag object
              const tagData = groupTag.tag || groupTag;
              return (
                <View
                  key={tagData.tagId || index}
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
                    {tagData.name || "N/A"}
                  </Text>
                </View>
              );
            })}
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
        {/* Chỉnh sửa nhóm - Only for leader */}
        {isLeader && (
          <TouchableOpacity
            style={{
              backgroundColor: colors.primary,
              paddingVertical: 16,
              borderRadius: radii.md,
              marginBottom: 12,
            }}
            onPress={() => navigation.navigate("EditGroup", { groupId, group })}
            disabled={actionLoading}
          >
            <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800", fontSize: 16 }}>
              Chỉnh sửa nhóm
            </Text>
          </TouchableOpacity>
        )}

        {/* Mời thành viên - Only for leader and when group is not full */}
        {isLeader && memberCount < group.maxMembers && (
          <TouchableOpacity
            style={{
              backgroundColor: colors.primary2,
              paddingVertical: 16,
              borderRadius: radii.md,
              marginBottom: 12,
            }}
            onPress={() => navigation.navigate("InviteMember", { groupId })}
            disabled={actionLoading}
          >
            <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800", fontSize: 16 }}>
              Mời thành viên
            </Text>
          </TouchableOpacity>
        )}

        {/* Tham gia nhóm - Only if user is NOT in group and NOT leader */}
        {!isUserInGroup && !isLeader && (
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
        )}

        {/* Rời nhóm - Only if user is in group OR is leader */}
        {(isUserInGroup || isLeader) && (
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
        )}
      </View>
    </ScrollView>
  );
}