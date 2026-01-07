import { useContext, useState, useCallback } from "react";
import { View, Text } from "react-native";
import { useFocusEffect } from "@react-navigation/native";
import Screen from "../src/ui/Screen";
import ProjectCard from "../src/components/ProjectCard";
import LoadingSpinner from "../src/components/LoadingSpinner";
import ErrorMessage from "../src/components/ErrorMessage";
import { colors, radii } from "../src/ui/theme";
import { useUser } from "../src/hooks";
import { AuthContext } from "../App";

export default function HomeScreen({ navigation }) {
  const { userId } = useContext(AuthContext);
  const { data: user, loading, error, refetch } = useUser(userId);
  const [refreshing, setRefreshing] = useState(false);

  const handleRefresh = useCallback(async () => {
    setRefreshing(true);
    await refetch();
    setRefreshing(false);
  }, [refetch]);

  // Reload data when screen comes into focus (e.g., returning from group details)
  useFocusEffect(
    useCallback(() => {
      if (userId && !loading) {
        refetch();
      }
    }, [userId, refetch, loading])
  );

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
    <Screen refreshing={refreshing} onRefresh={handleRefresh}>
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
