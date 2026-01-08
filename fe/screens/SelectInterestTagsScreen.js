import React, { useState, useEffect, useContext } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
} from "react-native";
import { colors, radii } from "../src/ui/theme";
import { getSuggestedTags, createTags } from "../src/api/tagService";
import { updateUserTags } from "../src/api/userService";
import { AuthContext } from "../App";
import TagSelector from "../src/components/TagSelector";

export default function SelectInterestTagsScreen({ navigation, route }) {
  const { userId } = useContext(AuthContext);
  const { fromOnboarding = false } = route.params || {};

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [allTags, setAllTags] = useState([]);
  const [selectedTags, setSelectedTags] = useState([]);
  const [creatingTag, setCreatingTag] = useState(false);

  useEffect(() => {
    loadTags();
  }, []);

  const loadTags = async () => {
    try {
      setLoading(true);
      const response = await getSuggestedTags(userId);
      if (response.code === 200) {
        setAllTags(response.result || []);
      }
    } catch (error) {
      console.error("Load tags error:", error);
      Alert.alert("Lỗi", "Không thể tải danh sách tag");
    } finally {
      setLoading(false);
    }
  };

  const handleCreateNewTag = async (tagName) => {
    setCreatingTag(true);
    try {
      const response = await createTags([{ name: tagName }]);
      if (response.code === 200 && response.result && response.result.length > 0) {
        const newTag = response.result[0];
        setAllTags([...allTags, newTag]);
        setSelectedTags([...selectedTags, newTag]);
        Alert.alert("Thành công", "Đã tạo tag mới!");
        return true;
      }
      return false;
    } catch (error) {
      console.error("Create tag error:", error);
      Alert.alert("Lỗi", error.response?.data?.message || "Không thể tạo tag mới");
      return false;
    } finally {
      setCreatingTag(false);
    }
  };

  const handleSave = async () => {
    if (selectedTags.length === 0) {
      Alert.alert("Thông báo", "Vui lòng chọn ít nhất 1 tag quan tâm");
      return;
    }

    try {
      setSaving(true);
      const response = await updateUserTags(userId, selectedTags);
      if (response.code === 200) {
        Alert.alert("Thành công", "Đã cập nhật tag quan tâm!", [
          {
            text: "OK",
            onPress: () => {
              if (fromOnboarding) {
                navigation.navigate("Tabs", { screen: "Home" });
              } else {
                navigation.goBack();
              }
            },
          },
        ]);
      }
    } catch (error) {
      console.error("Update tags error:", error);
      Alert.alert("Lỗi", error.response?.data?.message || "Không thể cập nhật tag");
    } finally {
      setSaving(false);
    }
  };

  const handleSkip = () => {
    if (fromOnboarding) {
      navigation.navigate("Tabs", { screen: "Home" });
    } else {
      navigation.goBack();
    }
  };

  if (loading) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center", backgroundColor: "#fff" }}>
        <ActivityIndicator size="large" color={colors.primary} />
        <Text style={{ marginTop: 16, color: colors.subtext }}>Đang tải tag...</Text>
      </View>
    );
  }

  return (
    <ScrollView style={{ flex: 1, backgroundColor: "#fff" }}>
      <View style={{ padding: 20 }}>
        <Text style={{ fontSize: 24, fontWeight: "900", color: colors.text, marginBottom: 8 }}>
          Chọn tag quan tâm
        </Text>
        <Text style={{ fontSize: 14, color: colors.subtext, marginBottom: 24 }}>
          Chọn các tag mà bạn quan tâm để tìm nhóm phù hợp hơn
        </Text>

        {/* Tag Selector */}
        <TagSelector
          allTags={allTags}
          selectedTags={selectedTags}
          onTagsChange={setSelectedTags}
          loading={loading}
          disabled={saving}
          onCreateTag={handleCreateNewTag}
          creatingTag={creatingTag}
        />

        {/* Buttons */}
        <View style={{ marginTop: 20, marginBottom: 40 }}>
          <TouchableOpacity
            style={{
              backgroundColor: colors.primary,
              paddingVertical: 16,
              borderRadius: radii.md,
              marginBottom: 12,
            }}
            onPress={handleSave}
            disabled={saving}
          >
            {saving ? (
              <ActivityIndicator color="#fff" />
            ) : (
              <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800", fontSize: 16 }}>
                Lưu
              </Text>
            )}
          </TouchableOpacity>

          {fromOnboarding && (
            <TouchableOpacity
              style={{
                paddingVertical: 16,
                borderRadius: radii.md,
              }}
              onPress={handleSkip}
            >
              <Text style={{ color: colors.subtext, textAlign: "center", fontWeight: "600" }}>
                Bỏ qua
              </Text>
            </TouchableOpacity>
          )}
        </View>
      </View>
    </ScrollView>
  );
}
