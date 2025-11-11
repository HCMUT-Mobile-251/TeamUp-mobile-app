import React from "react";
import { View, Text, TextInput } from "react-native";
import Screen from "../src/ui/Screen";
import Tag from "../src/components/Tag";
import ProjectCard from "../src/components/ProjectCard";
import { colors, radii } from "../src/ui/theme";

export default function SearchScreen({ navigation }) {
  return (
    <Screen>
      <Text style={{ fontSize: 22, fontWeight: "900", marginBottom: 12 }}>
        Tìm kiếm
      </Text>
      <View
        style={{
          borderWidth: 1,
          borderColor: "#E2E8F0",
          borderRadius: radii.lg,
          paddingHorizontal: 12,
          paddingVertical: 8,
          marginBottom: 12,
          backgroundColor: colors.white,
        }}
      >
        <TextInput placeholder="Tìm đề tài, thành viên" />
      </View>

      <Text style={{ fontWeight: "800", marginBottom: 8 }}>Tags phổ biến</Text>
      <View style={{ flexDirection: "row", flexWrap: "wrap" }}>
        {[
          "Machine learning",
          "Web Development",
          "Mobile app",
          "IoT",
          "UX/UI",
          "Data Science",
        ].map((t) => (
          <Tag key={t} label={t} />
        ))}
      </View>

      <Text style={{ marginTop: 16, fontWeight: "800" }}>Đề xuất</Text>
      {[1, 2, 3].map((i) => (
        <ProjectCard key={i} onPress={() => navigation.navigate("JoinGroup")} />
      ))}
      <Text
        onPress={() => navigation.navigate("AdvancedSearch")}
        style={{ marginTop: 8, alignSelf: "flex-end", color: colors.primary }}
      >
        Tìm kiếm nâng cao →
      </Text>
    </Screen>
  );
}
