import React, { useState, useEffect, useContext } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
  Modal,
  TextInput,
  FlatList,
} from "react-native";
import { Ionicons } from "@expo/vector-icons"; // Import Ionicons for close button
import { colors, radii } from "../src/ui/theme";
import { updateGroup } from "../src/api/groupService";
import {
  getGroupById,
  sendJoinRequest,
  leaveGroup,
  acceptJoinRequest,
  rejectJoinRequest,
  increaseMembers, // Add increaseMembers import
  transferLeadership,
} from "../src/api/groupService";
import { searchUsers } from "../src/api/userService"; // Add searchUsers import
import { AuthContext } from "../App";
import { normalizeStatus } from "../src/utils/statusUtils";

const MEMBER_STATUS = {
  JOINED: "JOINED",
  WAITING: "WAITING_APPROVAL",   // user xin vào
  PENDING: "PENDING_APPROVAL",   // leader mời
  REJECTED: "REJECTED",
};

export default function GroupInfoScreen({ route, navigation }) {
  const { groupId } = route.params;
  const { userId } = useContext(AuthContext);
  const [group, setGroup] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  // Invite Modal State
  const [inviteModalVisible, setInviteModalVisible] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);

  // Transfer Leadership Modal State
  const [transferModalVisible, setTransferModalVisible] = useState(false);
  const [selectedNewLeader, setSelectedNewLeader] = useState(null);

  useEffect(() => {
    loadGroupInfo();
  }, [groupId]);

  const loadGroupInfo = async () => {
    setLoading(true);
    try {
      const response = await getGroupById(groupId);
      if (response.code === 200) {
        // DEBUG: Log all members and their status
        console.log("=== GROUP DEBUG ===");
        console.log("Leader ID:", response.result.leaderId?.userId);
        console.log("Total groupMembers:", response.result.groupMembers?.length || 0);
        response.result.groupMembers?.forEach((m, i) => {
          console.log(`Member ${i + 1}: ${m.user?.userId} | Status: ${m.status}`);
        });
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

    Alert.alert("Tham gia nhóm", "Bạn có muốn gửi yêu cầu tham gia nhóm này?", [
      { text: "Hủy", style: "cancel" },
      {
        text: "Tham gia",
        onPress: async () => {
          setActionLoading(true);
          try {
            const response = await sendJoinRequest(groupId, {
              userId: userId,
              message: "Xin chào, tôi muốn tham gia nhóm này!",
            });
            if (response.code === 200) {
              Alert.alert("Thành công", "Đã gửi yêu cầu tham gia nhóm!");
              loadGroupInfo(); // Reload to see updated status
            } else {
              Alert.alert("Lỗi", response.message || "Không thể tham gia nhóm");
            }
          } catch (error) {
            console.error("Join group error:", error);
            Alert.alert(
              "Lỗi",
              error.response?.data?.message || "Có lỗi xảy ra"
            );
          } finally {
            setActionLoading(false);
          }
        },
      },
    ]);
  };

const handleLeaveGroup = async () => {
  // If user is leader, they must transfer leadership first
  if (isLeader) {
    Alert.alert(
      "Chuyển quyền leader",
      "Bạn là leader của nhóm. Vui lòng chuyển quyền leader cho thành viên khác trước khi rời nhóm.",
      [
        { text: "Hủy", style: "cancel" },
        {
          text: "Chuyển quyền",
          onPress: () => setTransferModalVisible(true),
        },
      ]
    );
    return;
  }

  Alert.alert(
    "Rời nhóm",
    "Bạn có chắc chắn muốn rời khỏi nhóm này?",
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
              Alert.alert("Thành công", "Bạn đã rời nhóm", [
                {
                  text: "OK",
                  onPress: () => {
                    navigation.navigate("Tabs", {
                      screen: "Home",
                      params: { refresh: true },
                    });
                  },
                },
              ]);
            } else {
              Alert.alert("Lỗi", response.message || "Không thể rời nhóm");
            }
          } catch (error) {
            console.error("Leave group error:", error);
            Alert.alert(
              "Lỗi",
              error.response?.data?.message || "Không thể rời nhóm"
            );
          } finally {
            setActionLoading(false);
          }
        },
      },
    ]
  );
};

  const handleTransferLeadership = async (newLeaderId) => {
    Alert.alert(
      "Xác nhận chuyển quyền",
      "Bạn có chắc chắn muốn chuyển quyền leader cho thành viên này? Sau khi chuyển, bạn có thể rời nhóm.",
      [
        { text: "Hủy", style: "cancel" },
        {
          text: "Chuyển quyền",
          onPress: async () => {
            console.log("CONFIRMED TRANSFER", {
              groupId,
              newLeaderId,
              courseId: group?.course?.courseId,
            });

            // ⚠️ payload đầy đủ để qua @Valid (quan trọng)
            const payload = {
              groupId, // bắt buộc
              leaderId: newLeaderId, // leader mới
              courseId: group?.course?.courseId, // bắt buộc vì BE setCourse()
              name: group?.name,
              topicName: group?.topicName,
              description: group?.description,
              maxMembers: group?.maxMembers,
              groupClass: group?.groupClass,
            };

            setActionLoading(true);
            try {
              const response = await transferLeadership(groupId, newLeaderId, group?.course?.courseId);
              if (response.code === 200) {
                Alert.alert("Thành công", "Đã chuyển quyền leader thành công!");
                setTransferModalVisible(false);
                setSelectedNewLeader(null);
                loadGroupInfo(); // Reload to see updated leader
              } else {
                Alert.alert("Lỗi", response.message || "Không thể chuyển quyền leader");
              }
            } catch (error) {
              console.error("Transfer leadership error:", error);
              Alert.alert(
                "Lỗi",
                error.response?.data?.message || "Có lỗi xảy ra khi chuyển quyền"
              );
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

  // --- Invite Member Logic ---
  const handleSearchUser = async (text) => {
    setSearchText(text);
    if (text.length < 2) {
      setSearchResults([]);
      return;
    }

    setIsSearching(true);
    try {
      const response = await searchUsers(text);
      if (response.code === 200) {
        // Filter out users who are already in the group (JOINED, WAITING, PENDING)
        // Note: The backend addMember also checks this, but filtering here improves UX
        const filtered = response.result.filter((u) => {
          const isMember = group.groupMembers?.some(
            (m) =>
              m.user?.userId === u.userId &&
              (m.status === "JOINED" ||
                m.status === "WAITING_APPROVAL" ||
                m.status === "PENDING_APPROVAL")
          );
          return !isMember;
        });
        setSearchResults(filtered);
      }
    } catch (error) {
      console.error("Search user error:", error);
    } finally {
      setIsSearching(false);
    }
  };

  const handleInviteUser = async (userToInvite) => {
    // Double check group limit
    if (memberCount >= group.maxMembers) {
      Alert.alert("Thông báo", "Nhóm đã đủ thành viên, không thể mời thêm.");
      return;
    }

    // Check if user is already in group (just in case frontend filter missed it or race condition)
    const existingMember = group.groupMembers?.find(
      (m) =>
        m.user?.userId === userToInvite.userId &&
        (m.status === "JOINED" ||
          m.status === "WAITING_APPROVAL" ||
          m.status === "PENDING_APPROVAL")
    );

    if (existingMember) {
      Alert.alert(
        "Thông báo",
        "Người dùng này đã tham gia hoặc đang chờ duyệt vào nhóm."
      );
      return;
    }

    Alert.alert(
      "Mời thành viên",
      `Bạn có muốn mời ${userToInvite.firstName} ${userToInvite.lastName} vào nhóm?`,
      [
        { text: "Hủy", style: "cancel" },
        {
          text: "Mời",
          onPress: async () => {
            setActionLoading(true);
            try {
              // decreaseMembers takes a list of userIds - wait, increaseMembers!
              const response = await increaseMembers(groupId, [
                userToInvite.userId,
              ]);
              if (response.code === 200) {
                Alert.alert("Thành công", "Đã gửi lời mời thành công!");
                setInviteModalVisible(false);
                setSearchText("");
                setSearchResults([]);
                loadGroupInfo(); // Reload group info
              } else {
                Alert.alert(
                  "Lỗi",
                  response.message || "Không thể mời thành viên"
                );
              }
            } catch (error) {
              console.error("Invite error:", error);
              // Custom error handling
              if (error.response?.data?.code === 1008) {
                // GROUP_FULL
                Alert.alert("Thông báo", "Nhóm đã đủ thành viên.");
              } else if (error.response?.data?.code === 1005) {
                // USER_ALREADY_IN_GROUP
                Alert.alert("Thông báo", "Người dùng này đã tham gia nhóm.");
              } else {
                Alert.alert(
                  "Lỗi",
                  error.response?.data?.message || "Có lỗi xảy ra khi mời."
                );
              }
            } finally {
              setActionLoading(false);
            }
          },
        },
      ]
    );
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
      <View
        style={{
          flex: 1,
          justifyContent: "center",
          alignItems: "center",
          padding: 20,
        }}
      >
        <Text style={{ fontSize: 16, color: colors.subtext }}>
          Không tìm thấy thông tin nhóm
        </Text>
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          style={{
            marginTop: 20,
            padding: 12,
            backgroundColor: colors.primary,
            borderRadius: radii.md,
          }}
        >
          <Text style={{ color: "#fff", fontWeight: "600" }}>Quay lại</Text>
        </TouchableOpacity>
      </View>
    );
  }


  // Check if user is in the group
  const userMember = group.groupMembers?.find(m => m.user?.userId === userId);
  const isUserInGroup = !!userMember && normalizeStatus(userMember.status) === "JOINED";
  const isWaitingApproval = !!userMember && normalizeStatus(userMember.status) === "PENDING";
  const isLeader = group.leaderId?.userId === userId;

  // Count members: JOINED members + leader (if leader is not in groupMembers)
  const joinedMembers = group.groupMembers?.filter(m => normalizeStatus(m.status) === "JOINED") || [];
  const leaderInMembers = joinedMembers.some(m => m.user?.userId === group.leaderId?.userId);
  const memberCount = joinedMembers.length + (leaderInMembers ? 0 : 1);

  const InfoRow = ({ label, value }) => (
    <View style={{ marginBottom: 12 }}>
      <Text
        style={{
          marginBottom: 6,
          color: "#666",
          fontWeight: "600",
          fontSize: 13,
        }}
      >
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
        <Text style={{ fontSize: 15, color: colors.text }}>
          {value || "N/A"}
        </Text>
      </View>
    </View>
  );

  const MemberItem = ({ member }) => {
    // Translate status to Vietnamese
    const getStatusText = (status) => {
      switch (status) {
        case "WAITING_APPROVAL":
          return "Đang chờ duyệt";
        case "PENDING_APPROVAL":
          return "Đã được mời"; // Or "Chờ chấp nhận"
        case "ACCEPTED": // Not an enum, but check just in case
        case "JOINED":
          return "Đã tham gia";
        case "REJECTED":
          return "Đã bị từ chối";
        case "LEFT":
          return "Đã rời nhóm";
        case "REMOVED":
          return "Đã bị xóa";
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
        <View
          style={{
            flexDirection: "row",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <View style={{ flex: 1 }}>
            {/* <Text style={{ fontWeight: "600", fontSize: 15 }}>
              {member.user?.firstName} {member.user?.lastName}
            </Text> */}
            <View style={{ flexDirection: "row", alignItems: "center" }}>
              <Text style={{ fontWeight: "600", fontSize: 15 }}>
                {member.user?.firstName} {member.user?.lastName}
              </Text>

              {member.user?.userId === group.leaderId?.userId && (
                <View
                  style={{
                    marginLeft: 8,
                    backgroundColor: colors.primary,
                    paddingHorizontal: 6,
                    paddingVertical: 2,
                    borderRadius: 6,
                  }}
                >
                  <Text
                    style={{ color: "#fff", fontSize: 10, fontWeight: "800" }}
                  >
                    LEADER
                  </Text>
                </View>
              )}
            </View>

            {(isLeader || member.status !== "LEFT") && (
              <Text
                style={{ color: colors.subtext, fontSize: 13, marginTop: 2 }}
              >
                {member.user?.email}
              </Text>
            )}
            {(isLeader || member.status !== "LEFT") && (
              <Text
                style={{ color: colors.subtext, fontSize: 12, marginTop: 2 }}
              >
                Trạng thái: {getStatusText(member.status)}
              </Text>
            )}

            {member.status === "JOINED" && (
              <Text
                style={{
                  color: colors.primary,
                  fontSize: 11,
                  marginTop: 2,
                  fontStyle: "italic",
                }}
              >
                Nhấn để xem thông tin liên lạc
              </Text>
            )}
          </View>
          {normalizeStatus(member.status) === "PENDING" && isLeader && (
            <View style={{ flexDirection: "row" }}>
              <TouchableOpacity
                onPress={() => handleApproveRequest(member.user?.userId)}
                style={{
                  padding: 8,
                  backgroundColor: "#10B981",
                  borderRadius: radii.sm,
                  marginRight: 8,
                }}
                disabled={actionLoading}
              >
                <Text
                  style={{ color: "#fff", fontSize: 12, fontWeight: "600" }}
                >
                  Duyệt
                </Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => handleRejectRequest(member.user?.userId)}
                style={{
                  padding: 8,
                  backgroundColor: "#EF4444",
                  borderRadius: radii.sm,
                }}
                disabled={actionLoading}
              >
                <Text
                  style={{ color: "#fff", fontSize: 12, fontWeight: "600" }}
                >
                  Từ chối
                </Text>
              </TouchableOpacity>
            </View>
          )}
        </View>
      </TouchableOpacity>
    );
  };

  return (
    <ScrollView
      contentContainerStyle={{ padding: 16 }}
      showsVerticalScrollIndicator={false}
    >
      <Text style={{ fontSize: 22, fontWeight: "800", marginBottom: 16 }}>
        Thông tin nhóm
      </Text>

      <InfoRow label="Tên nhóm" value={group.name} />
      <InfoRow label="Mã môn học" value={group.course?.courseId} />
      <InfoRow label="Tên môn học" value={group.course?.name} />
      <InfoRow label="Mã lớp" value={group.groupClass} />
      <InfoRow label="Học kỳ" value={group.semester?.toString()} />
      <InfoRow label="Tên đề tài" value={group.topicName} />
      <InfoRow
        label="Số lượng thành viên"
        value={`${memberCount}/${group.maxMembers}`}
      />

      <View style={{ marginBottom: 16 }}>
        <Text
          style={{
            marginBottom: 6,
            color: "#666",
            fontWeight: "600",
            fontSize: 13,
          }}
        >
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
      <Text
        style={{
          fontSize: 18,
          fontWeight: "800",
          marginTop: 8,
          marginBottom: 12,
        }}
      >
        Thành viên ({memberCount} / {group.maxMembers})
      </Text>

      {isUserInGroup || isLeader ? (
        group.groupMembers && group.groupMembers.length > 0 ? (
          group.groupMembers.map((member, index) => (
            <MemberItem key={index} member={member} group={group} />
          ))
        ) : (
          <Text
            style={{ color: colors.subtext, textAlign: "center", padding: 20 }}
          >
            Chưa có thành viên
          </Text>
        )
      ) : (
        <View
          style={{
            padding: 20,
            backgroundColor: "#F3F4F6",
            borderRadius: radii.md,
            alignItems: "center",
          }}
        >
          <Ionicons
            name="lock-closed-outline"
            size={24}
            color={colors.subtext}
            style={{ marginBottom: 8 }}
          />
          <Text style={{ color: colors.subtext, textAlign: "center" }}>
            Bạn cần tham gia nhóm để xem danh sách thành viên
          </Text>
        </View>
      )}

      {/* Invite Button - Visible if user is a member (JOINED) or Leader, and group is not full */}
      {/* As per requirement: "if not reached limit member -> can invite" */}
      {isLeader && memberCount < group.maxMembers && (
        <TouchableOpacity
          style={{
            flexDirection: "row",
            alignItems: "center",
            justifyContent: "center",
            backgroundColor: "#fff",
            borderWidth: 1,
            borderColor: colors.primary,
            borderStyle: "dashed",
            padding: 12,
            borderRadius: radii.md,
            marginTop: 8,
            marginBottom: 8,
          }}
          onPress={() => setInviteModalVisible(true)}
        >
          <Ionicons
            name="add-circle-outline"
            size={20}
            color={colors.primary}
            style={{ marginRight: 8 }}
          />
          <Text style={{ color: colors.primary, fontWeight: "600" }}>
            Mời thành viên
          </Text>
        </TouchableOpacity>
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
            <Text
              style={{
                color: "#fff",
                textAlign: "center",
                fontWeight: "800",
                fontSize: 16,
              }}
            >
              Chỉnh sửa nhóm
            </Text>
          </TouchableOpacity>
        )}

        {/* Tham gia nhóm - Only if user is NOT in group and NOT leader and NOT waiting */}
        {!isUserInGroup && !isLeader && !isWaitingApproval && (
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
              <Text
                style={{
                  color: "#fff",
                  textAlign: "center",
                  fontWeight: "800",
                  fontSize: 16,
                }}
              >
                Tham gia nhóm
              </Text>
            )}
          </TouchableOpacity>
        )}

        {/* Hủy yêu cầu - Only if user is WAITING_APPROVAL */}
        {isWaitingApproval && (
          <View>
            <View
              style={{
                backgroundColor: "#FEF3C7",
                padding: 12,
                borderRadius: radii.md,
                marginBottom: 12,
                borderWidth: 1,
                borderColor: "#F59E0B",
              }}
            >
              <Text
                style={{
                  color: "#B45309",
                  textAlign: "center",
                  fontWeight: "600",
                }}
              >
                Bạn đã gửi yêu cầu tham gia nhóm
              </Text>
            </View>
            <TouchableOpacity
              style={{
                backgroundColor: "#EF4444",
                paddingVertical: 16,
                borderRadius: radii.md,
                marginBottom: 12,
              }}
              onPress={() => {
                Alert.alert(
                  "Hủy yêu cầu",
                  "Bạn có chắc muốn hủy yêu cầu tham gia nhóm?",
                  [
                    { text: "Không", style: "cancel" },
                    {
                      text: "Hủy yêu cầu",
                      style: "destructive",
                      onPress: async () => {
                        setActionLoading(true);
                        try {
                          const response = await leaveGroup(groupId, userId);
                          if (response.code === 200) {
                            Alert.alert("Thành công", "Đã hủy yêu cầu!", [
                              { text: "OK", onPress: () => loadGroupInfo() },
                            ]);
                          } else {
                            Alert.alert(
                              "Lỗi",
                              response.message || "Không thể hủy yêu cầu"
                            );
                          }
                        } catch (error) {
                          console.error("Cancel request error:", error);
                          Alert.alert(
                            "Lỗi",
                            error.response?.data?.message || "Có lỗi xảy ra"
                          );
                        } finally {
                          setActionLoading(false);
                        }
                      },
                    },
                  ]
                );
              }}
              disabled={actionLoading}
            >
              <Text
                style={{
                  color: "#fff",
                  textAlign: "center",
                  fontWeight: "800",
                  fontSize: 16,
                }}
              >
                Hủy yêu cầu
              </Text>
            </TouchableOpacity>
          </View>
        )}

        {/* Chuyển quyền leader - Only for leader */}
        {isLeader && (
          <TouchableOpacity
            style={{
              backgroundColor: colors.primary,
              paddingVertical: 16,
              borderRadius: radii.md,
              marginBottom: 12,
            }}
            onPress={() => setTransferModalVisible(true)}
            disabled={actionLoading}
          >
            <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800", fontSize: 16 }}>
              Chuyển quyền leader
            </Text>
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
            <Text
              style={{
                color: "#fff",
                textAlign: "center",
                fontWeight: "800",
                fontSize: 16,
              }}
            >
              Rời nhóm
            </Text>
          </TouchableOpacity>
        )}
      </View>
      {/* Invite Modal */}
      <Modal
        visible={inviteModalVisible}
        animationType="slide"
        presentationStyle="pageSheet" // iOS style
        onRequestClose={() => setInviteModalVisible(false)}
      >
        <View style={{ flex: 1, padding: 16, paddingTop: 60 }}>
          <View
            style={{
              flexDirection: "row",
              justifyContent: "space-between",
              alignItems: "center",
              marginBottom: 20,
            }}
          >
            <Text style={{ fontSize: 20, fontWeight: "800" }}>
              Mời thành viên
            </Text>
            <TouchableOpacity onPress={() => setInviteModalVisible(false)}>
              <Text style={{ color: colors.primary, fontSize: 16 }}>Đóng</Text>
            </TouchableOpacity>
          </View>

          <View
            style={{
              flexDirection: "row",
              alignItems: "center",
              backgroundColor: "#F3F4F6",
              borderRadius: radii.md,
              paddingHorizontal: 12,
              marginBottom: 16,
            }}
          >
            <Ionicons name="search" size={20} color="#9CA3AF" />
            <TextInput
              style={{ flex: 1, padding: 12, fontSize: 16 }}
              placeholder="Nhập MSSV hoặc Email..."
              value={searchText}
              onChangeText={handleSearchUser}
              autoCapitalize="none"
            />
          </View>

          {isSearching ? (
            <ActivityIndicator
              color={colors.primary}
              style={{ marginTop: 20 }}
            />
          ) : (
            <FlatList
              data={searchResults}
              keyExtractor={(item) => item.userId}
              ListEmptyComponent={
                searchText.length > 0 ? (
                  <Text
                    style={{
                      textAlign: "center",
                      color: colors.subtext,
                      marginTop: 20,
                    }}
                  >
                    Không tìm thấy người dùng
                  </Text>
                ) : (
                  <Text
                    style={{
                      textAlign: "center",
                      color: colors.subtext,
                      marginTop: 20,
                    }}
                  >
                    Nhập thông tin để tìm kiếm
                  </Text>
                )
              }
              renderItem={({ item }) => (
                <TouchableOpacity
                  onPress={() => handleInviteUser(item)}
                  style={{
                    flexDirection: "row",
                    alignItems: "center",
                    padding: 12,
                    borderBottomWidth: 1,
                    borderBottomColor: "#E5E7EB",
                  }}
                >
                  <View
                    style={{
                      width: 40,
                      height: 40,
                      borderRadius: 20,
                      backgroundColor: colors.primary + "20",
                      alignItems: "center",
                      justifyContent: "center",
                      marginRight: 12,
                    }}
                  >
                    <Text style={{ color: colors.primary, fontWeight: "700" }}>
                      {item.firstName?.[0]}
                      {item.lastName?.[0]}
                    </Text>
                  </View>
                  <View>
                    <Text style={{ fontWeight: "600", fontSize: 15 }}>
                      {item.firstName} {item.lastName}
                    </Text>
                    <Text style={{ color: colors.subtext, fontSize: 13 }}>
                      {item.studentId} - {item.email}
                    </Text>
                  </View>
                </TouchableOpacity>
              )}
            />
          )}
        </View>
      </Modal>

      {/* Transfer Leadership Modal */}
      <Modal
        visible={transferModalVisible}
        animationType="slide"
        presentationStyle="pageSheet"
        onRequestClose={() => setTransferModalVisible(false)}
      >
        <View style={{ flex: 1, padding: 16, paddingTop: 60 }}>
          <View style={{ flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
            <Text style={{ fontSize: 20, fontWeight: "800" }}>Chuyển quyền leader</Text>
            <TouchableOpacity onPress={() => setTransferModalVisible(false)}>
              <Text style={{ color: colors.primary, fontSize: 16 }}>Đóng</Text>
            </TouchableOpacity>
          </View>

          <View style={{
            backgroundColor: "#FEF3C7",
            padding: 12,
            borderRadius: radii.md,
            marginBottom: 16,
            borderWidth: 1,
            borderColor: "#F59E0B"
          }}>
            <Text style={{ color: "#B45309", fontSize: 14, fontWeight: "600" }}>
              Chọn thành viên để chuyển quyền leader. Sau khi chuyển, bạn sẽ có thể rời nhóm.
            </Text>
          </View>

          <Text style={{ fontSize: 16, fontWeight: "700", marginBottom: 12 }}>
            Chọn thành viên:
          </Text>

          <FlatList
            data={joinedMembers.filter(m => m.user?.userId !== userId)}
            keyExtractor={(item, index) => item.user?.userId || index.toString()}
            ListEmptyComponent={
              <View style={{ alignItems: "center", padding: 20 }}>
                <Text style={{ color: colors.subtext, textAlign: "center" }}>
                  Không có thành viên nào để chuyển quyền. Vui lòng mời thêm thành viên vào nhóm trước.
                </Text>
              </View>
            }
            renderItem={({ item }) => (
              <TouchableOpacity
                onPress={() => {
                  setSelectedNewLeader(item.user?.userId);
                  handleTransferLeadership(item.user?.userId);
                }}
                style={{
                  flexDirection: "row",
                  alignItems: "center",
                  padding: 12,
                  backgroundColor: "#fff",
                  borderRadius: radii.md,
                  marginBottom: 8,
                  borderWidth: 1,
                  borderColor: selectedNewLeader === item.user?.userId ? colors.primary : "#E5E7EB"
                }}
              >
                <View style={{
                  width: 40,
                  height: 40,
                  borderRadius: 20,
                  backgroundColor: colors.primary + "20",
                  alignItems: "center",
                  justifyContent: "center",
                  marginRight: 12
                }}>
                  <Text style={{ color: colors.primary, fontWeight: "700" }}>
                    {item.user?.firstName?.[0]}{item.user?.lastName?.[0]}
                  </Text>
                </View>
                <View style={{ flex: 1 }}>
                  <Text style={{ fontWeight: "600", fontSize: 15 }}>
                    {item.user?.firstName} {item.user?.lastName}
                  </Text>
                  <Text style={{ color: colors.subtext, fontSize: 13 }}>
                    {item.user?.email}
                  </Text>
                </View>
                <Ionicons name="chevron-forward" size={20} color={colors.subtext} />
              </TouchableOpacity>
            )}
          />
        </View>
      </Modal>
    </ScrollView>
  );
}
