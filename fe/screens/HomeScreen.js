import { useContext, useState, useCallback, useEffect } from "react";
import { View, Text } from "react-native";
import { useFocusEffect } from "@react-navigation/native";
import Screen from "../src/ui/Screen";
import ProjectCard from "../src/components/ProjectCard";
import LoadingSpinner from "../src/components/LoadingSpinner";
import ErrorMessage from "../src/components/ErrorMessage";
import { colors, radii } from "../src/ui/theme";
import { useUser } from "../src/hooks";
import { AuthContext } from "../App";
import { normalizeStatus } from "../src/utils/statusUtils";

export default function HomeScreen({ navigation, route }) {
  const { userId } = useContext(AuthContext);
  const { data: user, loading, error, refetch } = useUser(userId);
  const [refreshing, setRefreshing] = useState(false);

  // Auto refresh when navigating back from other screens
  useFocusEffect(
    useCallback(() => {
      // Only refetch if there's a refresh param or if coming from create/edit group
      if (route.params?.refresh) {
        refetch();
        // Clear the param to avoid repeated refetches
        navigation.setParams({ refresh: undefined });
      }
    }, [route.params?.refresh, refetch, navigation])
  );

  const handleRefresh = useCallback(async () => {
    setRefreshing(true);
    await refetch();
    setRefreshing(false);
  }, [refetch]);

  // Extract groups from user data
  const userData = user?.result;

  useEffect(() => {
    if (userData?.groups) {
      console.log(
        "HomeScreen Groups Debug:",
        userData.groups.map((g) => ({ id: g.group?.groupId, status: g.status }))
      );
    }
  }, [userData]);

  const groups = userData?.groups
  ?.filter(gm => normalizeStatus(gm.status) === "JOINED")
  ?.map(gm => ({
    ...gm.group,
    memberStatus: normalizeStatus(gm.status),
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
          Hello! {userData?.firstName || "User"}
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
            currentUserId={userId}
            onPress={() =>
              navigation.navigate("GroupInfo", {
                groupId: group.groupId || group.id,
              })
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
