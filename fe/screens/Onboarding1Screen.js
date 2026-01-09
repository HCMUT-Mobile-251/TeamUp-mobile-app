import React, { useContext } from "react";
import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import { Ionicons } from "@expo/vector-icons";
import { colors, radii, shadow } from "../src/ui/theme";
import Button from "../src/ui/Button";
import { AuthContext } from "../App";

export default function Onboarding1Screen({ navigation }) {
  const { markOnboardingSeen } = useContext(AuthContext);

  const handleSkip = async () => {
    await markOnboardingSeen();
    navigation.navigate("Login");
  };

  const handleNext = () => {
    navigation.navigate("Onboarding2");
  };

  return (
    <View style={styles.container}>
      {/* Skip Button */}
      <TouchableOpacity style={styles.skipButton} onPress={handleSkip}>
        <Text style={styles.skipText}>Bỏ qua</Text>
      </TouchableOpacity>

      {/* Main Content */}
      <View style={styles.content}>
        {/* Illustration Area */}
        <View style={styles.illustrationContainer}>
          <LinearGradient
            colors={[colors.primary, colors.primary2]}
            style={styles.iconCircle}
            start={{ x: 0, y: 0 }}
            end={{ x: 1, y: 1 }}
          >
            <Ionicons name="search" size={80} color={colors.white} />
          </LinearGradient>

          {/* Decorative elements */}
          <View style={[styles.decorativeCircle, styles.circle1]} />
          <View style={[styles.decorativeCircle, styles.circle2]} />
          <View style={[styles.decorativeCircle, styles.circle3]} />
        </View>

        {/* Text Content */}
        <View style={styles.textContainer}>
          <Text style={styles.title}>Tìm kiếm và tham gia nhóm dễ dàng</Text>
          <Text style={styles.description}>
            Khám phá hàng ngàn nhóm đồ án với các chủ đề đa dạng. Tìm kiếm theo
            kỹ năng, sở thích và kết nối với những người có cùng đam mê.
          </Text>
        </View>

        {/* Pagination Dots */}
        <View style={styles.pagination}>
          <View style={[styles.dot, styles.dotActive]} />
          <View style={styles.dot} />
        </View>

        {/* Next Button */}
        <Button title="Tiếp theo" onPress={handleNext} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.bg,
  },
  skipButton: {
    position: "absolute",
    top: 60,
    right: 20,
    zIndex: 10,
    paddingVertical: 8,
    paddingHorizontal: 16,
  },
  skipText: {
    fontSize: 16,
    fontWeight: "600",
    color: colors.subtext,
  },
  content: {
    flex: 1,
    paddingHorizontal: 24,
    paddingTop: 120,
    paddingBottom: 40,
    justifyContent: "space-between",
  },
  illustrationContainer: {
    alignItems: "center",
    justifyContent: "center",
    height: 300,
    position: "relative",
  },
  iconCircle: {
    width: 180,
    height: 180,
    borderRadius: 90,
    alignItems: "center",
    justifyContent: "center",
    ...shadow.fab,
  },
  decorativeCircle: {
    position: "absolute",
    borderRadius: 1000,
    opacity: 0.1,
  },
  circle1: {
    width: 240,
    height: 240,
    backgroundColor: colors.primary,
    top: "50%",
    left: "50%",
    marginLeft: -120,
    marginTop: -120,
  },
  circle2: {
    width: 60,
    height: 60,
    backgroundColor: colors.pink,
    top: 40,
    right: 40,
  },
  circle3: {
    width: 40,
    height: 40,
    backgroundColor: colors.primary2,
    bottom: 60,
    left: 30,
  },
  textContainer: {
    alignItems: "center",
    paddingHorizontal: 16,
  },
  title: {
    fontSize: 28,
    fontWeight: "900",
    color: colors.text,
    textAlign: "center",
    marginBottom: 16,
    lineHeight: 36,
  },
  description: {
    fontSize: 16,
    fontWeight: "400",
    color: colors.subtext,
    textAlign: "center",
    lineHeight: 24,
  },
  pagination: {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center",
    gap: 8,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: colors.tagBg,
  },
  dotActive: {
    width: 24,
    height: 8,
    backgroundColor: colors.primary,
  },
});
