import React, { useEffect, useMemo, useState } from "react";
import { View, Text, TouchableOpacity, ActivityIndicator } from "react-native";
import Tag from "./Tag";
import { colors, radii, shadow } from "../ui/theme";
import { normalizeStatus } from "../utils/statusUtils";

export default function ProjectCard({
  data,
  title,
  tags,
  members,
  onPress,
  currentUserId,
}) {
  const [memberCountFromApi, setMemberCountFromApi] = useState(null);
  const [loadingMembers, setLoadingMembers] = useState(false);

  const groupId = data?.groupId || data?.id;

  // Title
  const displayTitle = data?.name || data?.topicName || title || "Đồ án";

  // Extract tags from groupTags array
  const displayTags = data?.groupTags?.map(gt => gt.tag?.name).filter(Boolean) ||
                      data?.tags?.map(t => t.name) ||
                      tags || [];

  // Calculate member count: CHỈ đếm groupMembers có status JOINED + leader
  const joinedMembers = data?.groupMembers?.filter(gm => normalizeStatus(gm.status) === "JOINED").length || 0;
  const memberCount = joinedMembers + (data?.leaderId?.userId || data?.leaderId ? 1 : 0);
  const displayMembers = data?.currentMembers || memberCount || members || 0;
  const maxMembers = data?.maxMembers || null;

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
      <View
        style={{
          flexDirection: "row",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <View style={{ flex: 1, marginRight: 12 }}>
          <Text style={{ fontSize: 16, fontWeight: "800", color: colors.text }}>
            {displayTitle}
          </Text>

          {data?.groupClass && (
            <Text style={{ fontSize: 14, color: colors.subtext, marginTop: 4 }}>
              Lớp: {data.groupClass}
            </Text>
          )}

          {!!(
            data?.course?.name ||
            data?.courseName ||
            data?.course?.courseId ||
            data?.courseId
          ) && (
            <Text style={{ fontSize: 13, color: colors.subtext, marginTop: 4 }}>
              Môn học:{" "}
              {data?.course?.name ||
                data?.courseName ||
                data?.course?.courseId ||
                data?.courseId}
            </Text>
          )}
        </View>
        <View style={{ flexDirection: "row", flexWrap: "wrap", marginTop: 6 }}>
          {isLeader ? (
            <View
              style={{
                backgroundColor: colors.blueSoft,
                paddingHorizontal: 10,
                paddingVertical: 4,
                borderRadius: 999,
                marginRight: 8,
                marginTop: 6,
              }}
            >
              <Text
                style={{
                  color: colors.primary,
                  fontWeight: "800",
                  fontSize: 12,
                }}
              >
                👑 LEADER
              </Text>
            </View>
          ) : null}
        </View>

        <TouchableOpacity
          activeOpacity={0.9}
          onPress={onPress}
          style={{
            backgroundColor: colors.pinkSoft,
            paddingHorizontal: 16,
            paddingVertical: 8,
            borderRadius: radii.md,
          }}
        >
          <Text style={{ color: colors.pink, fontWeight: "800" }}>Xem</Text>
        </TouchableOpacity>
      </View>

      {/* {displayTags.length > 0 && (
        <View style={{ flexDirection: "row", marginTop: 10, flexWrap: "wrap" }}>
          {displayTags.slice(0, 3).map((t, i) => (
            <Tag key={i} label={t} />
          ))}
          {displayTags.length > 3 && (
            <Tag label={`+${displayTags.length - 3}`} />
          )}
        </View>
      )} */}

      <View
        style={{ flexDirection: "row", alignItems: "center", marginTop: 10 }}
      >
        <Text style={{ color: colors.subtext }}>
          👥 {displayMembers}
          {maxMembers ? `/${maxMembers}` : ""}
        </Text>

        {loadingMembers && (
          <ActivityIndicator
            style={{ marginLeft: 8 }}
            size="small"
            color={colors.subtext}
          />
        )}

        {/* {data?.description && (
          <Text
            style={{ color: colors.subtext, marginLeft: 12, flex: 1 }}
            numberOfLines={1}
          >
            • {data.description}
          </Text>
        )} */}
      </View>
    </View>
  );
}
