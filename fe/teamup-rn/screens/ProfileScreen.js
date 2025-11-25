import React, { useContext } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  SafeAreaView,
} from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import Tag from "../src/components/Tag";
import { AuthContext } from "../App";
import { colors, radii, shadow } from "../src/ui/theme";

export default function ProfileScreen() {
  const { signOut } = useContext(AuthContext);

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
      <ScrollView contentContainerStyle={{ padding: 16 }}>
        <Text style={{ fontSize: 24, fontWeight: "900", color: colors.text }}>
          Thông tin cá nhân
        </Text>

        {/* Card thông tin */}
        <View
          style={[
            {
              backgroundColor: colors.white,
              borderRadius: radii.lg,
              padding: 20,
              marginTop: 16,
            },
            shadow.card,
          ]}
        >
          <Row label="Tên" value="Tam Hoàng" />
          <Row label="MSSV" value="1234567" />
          <Row label="Khoa" value="Khoa học và kỹ thuật máy tính" />
          <Row label="Số điện thoại" value="1234567890" />
          <Row label="Email" value="tamhoang123@gmail.com" />
        </View>

        {/* Tags quan tâm */}
        <Text
          style={{
            fontWeight: "800",
            fontSize: 18,
            marginTop: 24,
            marginBottom: 8,
            color: colors.text,
          }}
        >
          Tags quan tâm
        </Text>
        <View
          style={{
            flexDirection: "row",
            flexWrap: "wrap",
            backgroundColor: colors.white,
            borderRadius: radii.lg,
            padding: 16,
            ...shadow.card,
          }}
        >
          {["Web Development", "UX/UI", "IoT", "Mobile app"].map((t) => (
            <Tag key={t} label={t} />
          ))}
        </View>

        {/* Nút đăng xuất */}
        <TouchableOpacity
          activeOpacity={0.9}
          onPress={signOut}
          style={{ marginTop: 32, borderRadius: radii.lg, ...shadow.card }}
        >
          <LinearGradient
            colors={[colors.primary, colors.primary2]}
            start={{ x: 0, y: 0 }}
            end={{ x: 1, y: 1 }}
            style={{
              borderRadius: radii.lg,
              paddingVertical: 16,
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Text
              style={{
                color: colors.white,
                fontWeight: "800",
                fontSize: 16,
              }}
            >
              Đăng xuất
            </Text>
          </LinearGradient>
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}

/* Component nhỏ để render từng dòng */
const Row = ({ label, value }) => (
  <View style={{ marginBottom: 12 }}>
    <Text style={{ fontWeight: "700", color: colors.subtext }}>{label}</Text>
    <Text style={{ color: colors.text, marginTop: 2 }}>{value}</Text>
  </View>
);
