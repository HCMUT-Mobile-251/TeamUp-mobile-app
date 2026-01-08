import React from "react";
import { SafeAreaView, ScrollView, RefreshControl } from "react-native";
import { colors } from "./theme";

export default function Screen({ children, scroll = true, refreshing, onRefresh }) {
  if (scroll) {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
        <ScrollView
          contentContainerStyle={{ padding: 16 }}
          refreshControl={
            onRefresh ? (
              <RefreshControl
                refreshing={refreshing || false}
                onRefresh={onRefresh}
                tintColor={colors.primary}
              />
            ) : undefined
          }
        >
          {children}
        </ScrollView>
      </SafeAreaView>
    );
  }
  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
      {children}
    </SafeAreaView>
  );
}
