import React, { useState, useEffect, useContext } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  TextInput,
  SafeAreaView,
  ActivityIndicator,
  Alert,
} from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import { useNavigation } from "@react-navigation/native";
import { AuthContext } from "../App";
import { colors, radii, shadow } from "../src/ui/theme";
import {
  getTagSuggestions,
  searchTags,
  createTag,
} from "../src/api/tagService";
import { updateUserTags } from "../src/api/userService";

export default function SelectTagsScreen() {
  const { userId } = useContext(AuthContext);
  const navigation = useNavigation();

  const [searchQuery, setSearchQuery] = useState("");
  const [suggestedTags, setSuggestedTags] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  const [selectedTags, setSelectedTags] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [searching, setSearching] = useState(false);

  useEffect(() => {
    loadSuggestedTags();
  }, []);

  useEffect(() => {
    if (searchQuery.trim()) {
      handleSearch();
    } else {
      setSearchResults([]);
    }
  }, [searchQuery]);

  const loadSuggestedTags = async () => {
    setLoading(true);
    try {
      const response = await getTagSuggestions(userId);
      if (response?.code === 200 && response?.result) {
        setSuggestedTags(response.result);
        // Set selected tags based on user's current tags
        const userTags = response.result.filter((tag) => tag.isUserTag);
        setSelectedTags(userTags);
      }
    } catch (error) {
      console.error("Load tags error:", error);
      Alert.alert("Lỗi", "Không thể tải danh sách tags");
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) return;

    setSearching(true);
    try {
      const response = await searchTags(searchQuery);
      if (response?.code === 200 && response?.result) {
        setSearchResults(response.result);
      }
    } catch (error) {
      console.error("Search tags error:", error);
    } finally {
      setSearching(false);
    }
  };

  const handleCreateTag = async () => {
    if (!searchQuery.trim()) {
      Alert.alert("Thông báo", "Vui lòng nhập tên tag");
      return;
    }

    try {
      const response = await createTag(searchQuery.trim());
      if (response?.code === 200 && response?.result) {
        const newTag = response.result;

        // Add to selected tags if not already selected
        if (!selectedTags.some((tag) => tag.tagId === newTag.tagId)) {
          setSelectedTags([...selectedTags, newTag]);
        }

        // Add to suggested tags if not already there
        if (!suggestedTags.some((tag) => tag.tagId === newTag.tagId)) {
          setSuggestedTags([newTag, ...suggestedTags]);
        }

        setSearchQuery("");
        setSearchResults([]);

        Alert.alert("Thành công", "Đã tạo tag mới!");
      }
    } catch (error) {
      console.error("Create tag error:", error);
      Alert.alert("Lỗi", "Không thể tạo tag mới");
    }
  };

  const toggleTag = (tag) => {
    const isSelected = selectedTags.some((t) => t.tagId === tag.tagId);

    if (isSelected) {
      setSelectedTags(selectedTags.filter((t) => t.tagId !== tag.tagId));
    } else {
      setSelectedTags([...selectedTags, tag]);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      console.log("[SelectTagsScreen] Saving tags:", selectedTags);
      console.log("[SelectTagsScreen] Number of tags:", selectedTags.length);
      const response = await updateUserTags(userId, selectedTags);
      console.log("[SelectTagsScreen] Save response:", response);
      if (response?.code === 200) {
        Alert.alert("Thành công", "Đã cập nhật tags quan tâm!", [
          {
            text: "OK",
            onPress: () => navigation.navigate("Tabs", {
              screen: "Profile",
              params: { refresh: true }
            })
          },
        ]);
      }
    } catch (error) {
      console.error("[SelectTagsScreen] Update tags error:", error);
      console.error("[SelectTagsScreen] Error details:", error.response?.data);
      Alert.alert("Lỗi", "Không thể cập nhật tags");
    } finally {
      setSaving(false);
    }
  };

  const renderTag = (tag, isSelected) => (
    <TouchableOpacity
      key={tag.tagId}
      onPress={() => toggleTag(tag)}
      style={{
        backgroundColor: isSelected ? colors.primary : colors.white,
        borderRadius: radii.full,
        paddingVertical: 8,
        paddingHorizontal: 16,
        margin: 4,
        borderWidth: 1,
        borderColor: isSelected ? colors.primary : colors.subtext + "30",
      }}
    >
      <Text
        style={{
          color: isSelected ? colors.white : colors.text,
          fontWeight: isSelected ? "700" : "500",
        }}
      >
        {tag.name}
      </Text>
    </TouchableOpacity>
  );

  if (loading) {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
        <View
          style={{ flex: 1, justifyContent: "center", alignItems: "center" }}
        >
          <ActivityIndicator size="large" color={colors.primary} />
          <Text style={{ marginTop: 12, color: colors.subtext }}>
            Đang tải tags...
          </Text>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
      <View style={{ flex: 1 }}>
        {/* Header */}
        <View
          style={{
            flexDirection: "row",
            alignItems: "center",
            padding: 16,
            borderBottomWidth: 1,
            borderBottomColor: colors.subtext + "20",
          }}
        >
          <TouchableOpacity onPress={() => navigation.goBack()}>
            <Text style={{ fontSize: 24, color: colors.text }}>←</Text>
          </TouchableOpacity>
          <Text
            style={{
              fontSize: 20,
              fontWeight: "800",
              color: colors.text,
              marginLeft: 12,
              flex: 1,
            }}
          >
            Chọn tags quan tâm
          </Text>
        </View>

        {/* Search box */}
        <View style={{ padding: 16 }}>
          <View
            style={{
              flexDirection: "row",
              alignItems: "center",
              backgroundColor: colors.white,
              borderRadius: radii.md,
              paddingHorizontal: 12,
              ...shadow.card,
            }}
          >
            <TextInput
              value={searchQuery}
              onChangeText={setSearchQuery}
              placeholder="Tìm kiếm hoặc tạo tag mới..."
              style={{
                flex: 1,
                paddingVertical: 12,
                fontSize: 16,
                color: colors.text,
              }}
              placeholderTextColor={colors.subtext}
            />
            {searching && <ActivityIndicator size="small" color={colors.primary} />}
          </View>

          {/* Create tag button */}
          {searchQuery.trim() && !searchResults.some((tag) => tag.name.toLowerCase() === searchQuery.toLowerCase()) && (
            <TouchableOpacity
              onPress={handleCreateTag}
              style={{
                backgroundColor: colors.pink,
                borderRadius: radii.md,
                paddingVertical: 12,
                alignItems: "center",
                marginTop: 8,
              }}
            >
              <Text style={{ color: colors.white, fontWeight: "700" }}>
                Tạo tag "{searchQuery}"
              </Text>
            </TouchableOpacity>
          )}
        </View>

        <ScrollView contentContainerStyle={{ padding: 16 }}>
          {/* Selected tags */}
          {selectedTags.length > 0 && (
            <View style={{ marginBottom: 24 }}>
              <Text
                style={{
                  fontWeight: "800",
                  fontSize: 16,
                  marginBottom: 12,
                  color: colors.text,
                }}
              >
                Tags đã chọn ({selectedTags.length})
              </Text>
              <View
                style={{
                  flexDirection: "row",
                  flexWrap: "wrap",
                  backgroundColor: colors.white,
                  borderRadius: radii.lg,
                  padding: 12,
                  ...shadow.card,
                }}
              >
                {selectedTags.map((tag) => renderTag(tag, true))}
              </View>
            </View>
          )}

          {/* Search results */}
          {searchQuery.trim() && searchResults.length > 0 && (
            <View style={{ marginBottom: 24 }}>
              <Text
                style={{
                  fontWeight: "800",
                  fontSize: 16,
                  marginBottom: 12,
                  color: colors.text,
                }}
              >
                Kết quả tìm kiếm
              </Text>
              <View
                style={{
                  flexDirection: "row",
                  flexWrap: "wrap",
                  backgroundColor: colors.white,
                  borderRadius: radii.lg,
                  padding: 12,
                  ...shadow.card,
                }}
              >
                {searchResults.map((tag) =>
                  renderTag(
                    tag,
                    selectedTags.some((t) => t.tagId === tag.tagId)
                  )
                )}
              </View>
            </View>
          )}

          {/* Suggested tags */}
          <View>
            <Text
              style={{
                fontWeight: "800",
                fontSize: 16,
                marginBottom: 12,
                color: colors.text,
              }}
            >
              Tags gợi ý
            </Text>
            <View
              style={{
                flexDirection: "row",
                flexWrap: "wrap",
                backgroundColor: colors.white,
                borderRadius: radii.lg,
                padding: 12,
                ...shadow.card,
              }}
            >
              {suggestedTags.map((tag) =>
                renderTag(
                  tag,
                  selectedTags.some((t) => t.tagId === tag.tagId)
                )
              )}
            </View>
          </View>
        </ScrollView>

        {/* Save button */}
        <View style={{ padding: 16 }}>
          <TouchableOpacity
            activeOpacity={0.9}
            onPress={handleSave}
            disabled={saving}
            style={{ borderRadius: radii.lg, ...shadow.card }}
          >
            <LinearGradient
              colors={[colors.primary, colors.pink]}
              start={{ x: 0, y: 0 }}
              end={{ x: 1, y: 1 }}
              style={{
                borderRadius: radii.lg,
                paddingVertical: 16,
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              {saving ? (
                <ActivityIndicator size="small" color={colors.white} />
              ) : (
                <Text
                  style={{
                    color: colors.white,
                    fontWeight: "800",
                    fontSize: 16,
                  }}
                >
                  Lưu thay đổi
                </Text>
              )}
            </LinearGradient>
          </TouchableOpacity>
        </View>
      </View>
    </SafeAreaView>
  );
}
