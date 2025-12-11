import React from "react";
import {
  View,
  Text,
  TextInput,
  ScrollView,
  TouchableOpacity,
} from "react-native";

export default function JoinGroupScreen() {
  return (
    <ScrollView contentContainerStyle={{ padding: 16 }}>
      <Text style={{ fontSize: 22, fontWeight: "800", marginBottom: 12 }}>
        Tham Gia nhóm
      </Text>
      {[
        "Mã môn học",
        "Tên môn học",
        "Mã lớp",
        "Học kỳ",
        "Tên nhóm",
        "Tên đề tài",
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
      <Text style={{ marginBottom: 6, color: "#666" }}>Miêu tả đề tài</Text>
      <View
        style={{
          borderWidth: 1,
          borderColor: "#eee",
          borderRadius: 16,
          paddingHorizontal: 12,
          paddingVertical: 12,
          minHeight: 90,
        }}
      >
        <TextInput placeholder="Miêu tả..." multiline />
      </View>
      <Text
        style={{
          fontSize: 16,
          fontWeight: "800",
          marginTop: 16,
          marginBottom: 8,
        }}
      >
        Lời chào, lý do vào nhóm
      </Text>
      <View
        style={{
          borderWidth: 1,
          borderColor: "#eee",
          borderRadius: 16,
          paddingHorizontal: 12,
          paddingVertical: 12,
        }}
      >
        <TextInput placeholder="Lời chào..." multiline />
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
          Gửi yêu cầu
        </Text>
      </TouchableOpacity>
    </ScrollView>
  );
}
