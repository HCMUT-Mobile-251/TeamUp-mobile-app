import React from "react";
import { Text, TouchableOpacity } from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import { colors, radii, shadow } from "./theme";

export default function Button({ title, onPress, disabled, style, textStyle }) {
  return (
    <TouchableOpacity
      activeOpacity={0.9}
      onPress={onPress}
      disabled={disabled}
      style={[{ borderRadius: radii.lg }, style]}
    >
      <LinearGradient
        colors={[colors.primary, colors.primary2]}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        style={[
          { paddingVertical: 16, borderRadius: radii.lg, alignItems: "center" },
          shadow.card,
        ]}
      >
        <Text
          style={[
            { color: colors.white, fontWeight: "800", fontSize: 16 },
            textStyle,
          ]}
        >
          {title}
        </Text>
      </LinearGradient>
    </TouchableOpacity>
  );
}
