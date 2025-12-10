import React from "react";
import {
  View,
  Text,
  TextInput,
  ScrollView,
  TouchableOpacity,
} from "react-native";

export default function CreateGroupScreen() {
  return (
    <ScrollView contentContainerStyle={{ padding: 16 }}>
      <Text style={{ fontSize: 22, fontWeight: "800", marginBottom: 12 }}>
        Tạo nhóm
      </Text>
      {[
        "Mã môn học",
        "Tên môn học",
        "Mã lớp",
        "Học kỳ",
        "Tên nhóm",
        "Tên đề tài",
        "Số lượng thành viên",
        "Miêu tả đề tài",
      ].map((label, idx) => (
        <View key={idx} style={{ marginBottom: 12 }}>
          <Text style={{ marginBottom: 6, color: "#666" }}>{label}</Text>
          <View
            style={{
              borderWidth: 1,
              borderColor: "#eee",
              borderRadius: 16,
              paddingHorizontal: 12,
              paddingVertical: 12,
            }}
          >
            <TextInput placeholder={label} />
          </View>
        </View>
      ))}
      <TouchableOpacity
        style={{
          marginTop: 8,
          backgroundColor: "#2b5cff",
          paddingVertical: 16,
          borderRadius: 16,
        }}
      >
        <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800" }}>
          Tạo nhóm
        </Text>
      </TouchableOpacity>
    </ScrollView>
  );
}
