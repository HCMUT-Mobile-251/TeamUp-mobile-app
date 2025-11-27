import React, { useContext } from "react";
import { View, Text, TouchableOpacity } from "react-native";
import Screen from "../src/ui/Screen";
import ProjectCard from "../src/components/ProjectCard";
import LoadingSpinner from "../src/components/LoadingSpinner";
import ErrorMessage from "../src/components/ErrorMessage";
import { colors, radii, shadow } from "../src/ui/theme";
import { useAllGroups } from "../src/hooks";
import { AuthContext } from "../App";

export default function HomeScreen({ navigation }) {
  const { token } = useContext(AuthContext);
  const { data: groups, loading, error, refetch } = useAllGroups(0, 20);

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
          Hello! Tam Hoàng
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
