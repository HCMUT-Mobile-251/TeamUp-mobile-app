import React from "react";
import { View, Text, TouchableOpacity } from "react-native";
import Screen from "../src/ui/Screen";
import ProjectCard from "../src/components/ProjectCard";
import { colors, radii, shadow } from "../src/ui/theme";

export default function HomeScreen({ navigation }) {
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
            Số đề tài đang tham gia: 5
          </Text>
        </View>
      </View>
      {[1, 2, 3].map((i) => (
        <ProjectCard key={i} onPress={() => navigation.navigate("GroupInfo")} />
      ))}
    </Screen>
  );
}
