import React from "react";
import {
  View,
  Text,
  TextInput,
  ScrollView,
  TouchableOpacity,
} from "react-native";

export default function GroupInfoScreen() {
  return (
    <ScrollView contentContainerStyle={{ padding: 16 }}>
      <Text style={{ fontSize: 22, fontWeight: "800", marginBottom: 12 }}>
        Thông tin nhóm
      </Text>
      {[
        "Mã môn học",
        "Tên môn học",
        "Mã lớp",
        "Học kỳ",
        "Tên nhóm",
        "Tên đề tài",
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
            <TextInput value={label} />
          </View>
        </View>
      ))}
      <Text
        style={{
          fontSize: 18,
          fontWeight: "800",
          marginTop: 8,
          marginBottom: 8,
        }}
      >
        Thành viên
      </Text>
      <View style={{ backgroundColor: "#fff", borderRadius: 16, padding: 12 }}>
        <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
          <Text>Nguyễn Văn A</Text>
          <Text>Leader</Text>
        </View>
      </View>
      <TouchableOpacity
        style={{
          marginTop: 20,
          backgroundColor: "#2b5cff",
          paddingVertical: 16,
          borderRadius: 16,
        }}
      >
        <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800" }}>
          Lưu
        </Text>
      </TouchableOpacity>
    </ScrollView>
  );
}
