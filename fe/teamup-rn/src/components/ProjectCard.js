import React from "react";
import { View, Text, TouchableOpacity } from "react-native";
import Tag from "./Tag";
import { colors, radii, shadow } from "../ui/theme";

export default function ProjectCard({
  title = "Đồ án AI",
  tags = ["AI", "Machine learning"],
  members = 3,
  onPress,
}) {
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
        <Text style={{ fontSize: 16, fontWeight: "800", color: colors.text }}>
          {title}
        </Text>
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
          <Text style={{ color: colors.pink, fontWeight: "800" }}>
            Tham gia
          </Text>
        </TouchableOpacity>
      </View>

      <View style={{ flexDirection: "row", marginTop: 10, flexWrap: "wrap" }}>
        {tags.map((t, i) => (
          <Tag key={i} label={t} />
        ))}
      </View>

      <View
        style={{ flexDirection: "row", alignItems: "center", marginTop: 10 }}
      >
        <Text style={{ color: colors.subtext }}>👥 {members}</Text>
      </View>
    </View>
  );
}
