import React from "react";
import { View, Text, TouchableOpacity } from "react-native";
import Screen from "../src/ui/Screen";
import Tag from "../src/components/Tag";
import { colors, radii, shadow } from "../src/ui/theme";

function Card() {
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
      <Text style={{ fontSize: 16, fontWeight: "800" }}>Đồ án AI</Text>
      <View style={{ flexDirection: "row", flexWrap: "wrap", marginTop: 6 }}>
        {["AI", "Machine learning", "Design", "Web Dev"].map((t) => (
          <Tag key={t} label={t} />
        ))}
      </View>
      <View
        style={{
          marginTop: 10,
          backgroundColor: colors.white,
          borderRadius: radii.md,
          padding: 12,
        }}
      >
        <Text>Mình đang tìm nhóm, mình biết về AI khá tốt</Text>
      </View>
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
          }}
        >
          <Text style={{ color: colors.pink, fontWeight: "800" }}>Từ chối</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={{
            backgroundColor: colors.blueSoft,
            paddingHorizontal: 16,
            paddingVertical: 10,
            borderRadius: radii.md,
          }}
        >
          <Text style={{ color: colors.primary, fontWeight: "800" }}>
            Chấp thuận
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

export default function NotificationsScreen() {
  return (
    <Screen>
      <Text style={{ fontSize: 22, fontWeight: "900", marginBottom: 12 }}>
        Thông báo
      </Text>
      {[1, 2].map((i) => (
        <Card key={i} />
      ))}
    </Screen>
  );
}
