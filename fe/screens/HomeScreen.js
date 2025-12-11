import { useState, useEffect } from "react";
import { View, Text, Platform } from "react-native";
import * as SecureStore from "expo-secure-store";
import Screen from "../src/ui/Screen";
import ProjectCard from "../src/components/ProjectCard";
import LoadingSpinner from "../src/components/LoadingSpinner";
import ErrorMessage from "../src/components/ErrorMessage";
import { colors, radii } from "../src/ui/theme";
import { useUser } from "../src/hooks";

export default function HomeScreen({ navigation }) {
  const [userId, setUserId] = useState(null);

  // Load userId from storage
  useEffect(() => {
    (async () => {
      try {
        let id;
        if (Platform.OS === 'web') {
          id = localStorage.getItem("user_id");
        } else {
          id = await SecureStore.getItemAsync("user_id");
        }
        if (!id) {
          // Fallback to hardcoded UUID if not found
          id = "af4937ad-0d3b-4bfe-ba61-ba984f266c48";
        }
        setUserId(id);
      } catch (error) {
        console.error("Error loading userId:", error);
        setUserId("af4937ad-0d3b-4bfe-ba61-ba984f266c48");
      }
    })();
  }, []);

  const { data: user, loading, error, refetch } = useUser(userId);

  // Extract groups from user data
  const groups = user?.groups?.map(gm => ({
    ...gm.group,
    memberStatus: gm.status,
    joinTime: gm.time
  })) || [];

  // Loading state
  if (loading) {
    return <LoadingSpinner message="Đang tải danh sách nhóm..." />;
  }

  // Error state
  if (error) {
    return <ErrorMessage message={error} onRetry={refetch} />;
  }

  // Calculate user's group count
  const userGroupCount = groups?.length || 0;

  return (
    <Screen>
      <View style={{ marginBottom: 16 }}>
        <Text style={{ fontSize: 22, fontWeight: "900", color: colors.text }}>
          Hello! {user?.firstName || "User"}
        </Text>
        <View
          style={[
            {
              backgroundColor: "#E7EEFF",
              padding: 12,
              marginTop: 10,
              borderRadius: radii.md,
            },
          ]}
        >
          <Text style={{ fontWeight: "800", color: colors.primary }}>
            Số đề tài đang có: {userGroupCount}
          </Text>
        </View>
      </View>

      {/* Display groups */}
      {groups && groups.length > 0 ? (
        groups.map((group) => (
          <ProjectCard
            key={group.groupId || group.id}
            data={group}
            onPress={() =>
              navigation.navigate("GroupInfo", { groupId: group.groupId || group.id })
            }
          />
        ))
      ) : (
        <View style={{ padding: 24, alignItems: "center" }}>
          <Text style={{ color: colors.subtext, fontSize: 16 }}>
            Chưa có nhóm nào
          </Text>
        </View>
      )}
    </Screen>
  );
}
