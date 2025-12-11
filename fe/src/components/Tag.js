import React from "react";
import { Text, View } from "react-native";
import { colors, radii } from "../ui/theme";

export default function Tag({ label, selected = false }) {
  return (
    <View
      style={{
        paddingHorizontal: 12,
        paddingVertical: 6,
        backgroundColor: selected ? colors.primary : colors.tagBg,
        borderRadius: radii.xl,
        marginRight: 8,
        marginBottom: 8,
      }}
    >
      <Text style={{ color: selected ? colors.white : colors.primary, fontWeight: "700", fontSize: 12 }}>
        #{label}
      </Text>
    </View>
  );
}
