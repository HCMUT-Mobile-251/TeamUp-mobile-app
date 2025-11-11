import React from "react";
import {
  View,
  Text,
  TextInput,
  ScrollView,
  TouchableOpacity,
} from "react-native";

export default function AdvancedSearchScreen() {
  return (
    <ScrollView contentContainerStyle={{ padding: 16 }}>
      <Text style={{ fontSize: 22, fontWeight: "800", marginBottom: 12 }}>
        Tìm kiếm nâng cao
      </Text>
      {["Theo mã môn", "Theo nhóm lớp", "Theo tên nhóm", "Theo tên đề tài"].map(
        (ph, idx) => (
          <View
            key={idx}
            style={{
              borderWidth: 1,
              borderColor: "#ddd",
              borderRadius: 16,
              paddingHorizontal: 12,
              paddingVertical: 8,
              marginBottom: 12,
            }}
          >
            <TextInput placeholder="Tìm kiếm" />
          </View>
        )
      )}
      <TouchableOpacity
        style={{
          backgroundColor: "#2b5cff",
          paddingVertical: 16,
          borderRadius: 16,
          marginTop: 12,
        }}
      >
        <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800" }}>
          Tìm kiếm
        </Text>
      </TouchableOpacity>
    </ScrollView>
  );
}
