import React from "react";
import { SafeAreaView, ScrollView, RefreshControl, Platform, StatusBar, View } from "react-native";
import Constants from "expo-constants";
import { colors } from "./theme";

export default function Screen({ children, scroll = true, refreshing, onRefresh }) {
  // Calculate safe area padding for Android
  const statusBarHeight = Platform.OS === "android" ? Constants.statusBarHeight || StatusBar.currentHeight || 0 : 0;

  if (scroll) {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
        <View style={{ paddingTop: statusBarHeight, flex: 1 }}>
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
        </View>
      </SafeAreaView>
    );
  }
  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
      <View style={{ paddingTop: statusBarHeight, flex: 1 }}>
        {children}
      </View>
    </SafeAreaView>
  );
}
