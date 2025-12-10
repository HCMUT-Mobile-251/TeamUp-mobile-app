import React from "react";
import { View, ActivityIndicator, Text, StyleSheet } from "react-native";
import { colors } from "../ui/theme";

export default function LoadingSpinner({ message = "Đang tải..." }) {
  return (
    <View style={styles.container}>
      <ActivityIndicator size="large" color={colors.primary} />
      <Text style={styles.text}>{message}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: colors.bg,
  },
  text: {
    marginTop: 16,
    fontSize: 16,
    color: colors.subtext,
    fontWeight: "600",
  },
});
