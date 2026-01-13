import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
  ScrollView,
  SafeAreaView,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
// import { KeyboardAvoidingView, Platform } from "react-native";

import { colors, radii } from "../src/ui/theme";
import { updateGroup, updateGroupTags } from "../src/api/groupService";
import { searchTags, createTag } from "../src/api/tagService";

const InputField = React.memo(
  ({
    label,
    value,
    onChangeText,
    placeholder,
    multiline = false,
    keyboardType = "default",
    editable = true,
  }) => (
    <View style={{ marginBottom: 16 }}>
      <Text
        style={{
          marginBottom: 8,
          fontSize: 14,
          fontWeight: "600",
          color: "#333",
        }}
      >
        {label}
      </Text>
      <TextInput
        style={{
          borderWidth: 1,
          borderColor: "#E2E8F0",
          borderRadius: radii.md,
          paddingHorizontal: 12,
          paddingVertical: multiline ? 12 : 14,
          fontSize: 15,
          backgroundColor: "#fff",
          color: colors.text,
          minHeight: multiline ? 100 : undefined,
          textAlignVertical: multiline ? "top" : "center",
        }}
        value={value}
        onChangeText={onChangeText}
        placeholder={placeholder}
        placeholderTextColor="#94A3B8"
        multiline={multiline}
        keyboardType={keyboardType}
        editable={editable}
      />
    </View>
  )
);

export default function EditGroupScreen({ route, navigation }) {
  const { groupId, group } = route.params;

  const [formData, setFormData] = useState({
    name: group?.name || "",
    description: group?.description || "",
    maxMembers: group?.maxMembers?.toString() || "",
    topicName: group?.topicName || "",
    groupClass: group?.groupClass || "",
  });

  const [loading, setLoading] = useState(false);

  // Tag selection states
  const [selectedTags, setSelectedTags] = useState([]);
  const [tagSearchQuery, setTagSearchQuery] = useState("");
  const [tagSearchResults, setTagSearchResults] = useState([]);
  const [tagSearching, setTagSearching] = useState(false);

  // Initialize selected tags from group data
  useEffect(() => {
    if (group?.groupTags) {
      const tags = group.groupTags.map((gt) => gt.tag || gt);
      setSelectedTags(tags.filter(Boolean));
    }
  }, [group]);

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  // Tag search and selection handlers
  useEffect(() => {
    if (tagSearchQuery.trim().length >= 2) {
      handleTagSearch();
    } else {
      setTagSearchResults([]);
    }
  }, [tagSearchQuery]);

  const handleTagSearch = async () => {
    setTagSearching(true);
    try {
      const response = await searchTags(tagSearchQuery);
      if (response.code === 200 && response.result) {
        setTagSearchResults(response.result);
      }
    } catch (error) {
      console.error("Tag search error:", error);
    } finally {
      setTagSearching(false);
    }
  };

  const handleCreateNewTag = async () => {
    if (!tagSearchQuery.trim()) {
      Alert.alert("Thông báo", "Vui lòng nhập tên tag");
      return;
    }

    try {
      const response = await createTag(tagSearchQuery.trim());
      if (response?.code === 200 && response?.result) {
        const newTag = response.result;
        if (!selectedTags.some((tag) => tag.tagId === newTag.tagId)) {
          setSelectedTags([...selectedTags, newTag]);
        }
        setTagSearchQuery("");
        setTagSearchResults([]);
        Alert.alert("Thành công", "Đã tạo tag mới!");
      }
    } catch (error) {
      console.error("Create tag error:", error);
      Alert.alert("Lỗi", "Không thể tạo tag mới");
    }
  };

  const handleSelectTag = (tag) => {
    if (!selectedTags.some((t) => t.tagId === tag.tagId)) {
      setSelectedTags([...selectedTags, tag]);
    }
    setTagSearchQuery("");
    setTagSearchResults([]);
  };

  const handleRemoveTag = (tagId) => {
    setSelectedTags(selectedTags.filter((tag) => tag.tagId !== tagId));
  };

  const validateForm = () => {
    if (!formData.name.trim()) {
      Alert.alert("Lỗi", "Tên nhóm không được để trống");
      return false;
    }

    if (!formData.topicName.trim()) {
      Alert.alert("Lỗi", "Tên đề tài không được để trống");
      return false;
    }

    if (!formData.groupClass.trim()) {
      Alert.alert("Lỗi", "Mã lớp không được để trống");
      return false;
    }

    const maxMembers = parseInt(formData.maxMembers);
    if (isNaN(maxMembers) || maxMembers < 1 || maxMembers > 10) {
      Alert.alert("Lỗi", "Số lượng thành viên phải từ 1 đến 10");
      return false;
    }

    return true;
  };

  const handleSave = async () => {
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      const updateData = {
        groupId,
        ...formData,
        maxMembers: parseInt(formData.maxMembers),
        leaderId: group?.leaderId?.userId || group?.leaderId,
        courseId: group?.course?.courseId,
      };

      const response = await updateGroup(groupId, updateData);
      if (response.code === 200) {
        // Update group tags
        try {
          await updateGroupTags(groupId, selectedTags);
          console.log("Group tags updated successfully");
        } catch (tagError) {
          console.error("Error updating group tags:", tagError);
          // Continue even if tag update fails
        }

        Alert.alert("Thành công", "Đã cập nhật thông tin nhóm!", [
          {
            text: "OK",
            onPress: () => {
              // Navigate back to home and trigger refresh
              navigation.navigate("Tabs", {
                screen: "Home",
                params: { refresh: true },
              });
            },
          },
        ]);
      } else {
        Alert.alert("Lỗi", response.message || "Không thể cập nhật nhóm");
      }
    } catch (error) {
      console.error("Update group error:", error);
      Alert.alert("Lỗi", error.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: colors.bg }}>
      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        keyboardVerticalOffset={Platform.OS === "ios" ? 80 : 0}
        style={{ flex: 1 }}
      >
        <ScrollView
          contentContainerStyle={{ padding: 16 }}
          showsVerticalScrollIndicator={false}
          keyboardShouldPersistTaps="always"
          // contentContainerStyle={{ paddingBottom: 140 }}
        >
          <Text
            style={{
              fontSize: 24,
              fontWeight: "900",
              marginBottom: 20,
              color: colors.text,
            }}
          >
            Chỉnh sửa nhóm
          </Text>

          <InputField
            label="Tên nhóm *"
            value={formData.name}
            onChangeText={(value) => handleInputChange("name", value)}
            placeholder="Nhập tên nhóm"
            editable={!loading}
          />

          <InputField
            label="Tên đề tài *"
            value={formData.topicName}
            onChangeText={(value) => handleInputChange("topicName", value)}
            placeholder="Nhập tên đề tài"
            editable={!loading}
          />

          <InputField
            label="Mã lớp *"
            value={formData.groupClass}
            onChangeText={(value) => handleInputChange("groupClass", value)}
            placeholder="Ví dụ: CC01"
            editable={!loading}
          />

          <InputField
            label="Số lượng thành viên tối đa *"
            value={formData.maxMembers}
            onChangeText={(value) => handleInputChange("maxMembers", value)}
            placeholder="1-10"
            keyboardType="numeric"
            editable={!loading}
          />

          <InputField
            label="Miêu tả đề tài"
            value={formData.description}
            onChangeText={(value) => handleInputChange("description", value)}
            placeholder="Mô tả chi tiết về đề tài..."
            multiline
            editable={!loading}
          />

          {/* Tag Selection */}
          <View style={{ marginBottom: 16 }}>
            <Text
              style={{
                marginBottom: 8,
                fontSize: 14,
                fontWeight: "600",
                color: "#333",
              }}
            >
              Tags liên quan
            </Text>

            {/* Selected Tags */}
            {selectedTags.length > 0 && (
              <View
                style={{
                  flexDirection: "row",
                  flexWrap: "wrap",
                  marginBottom: 8,
                }}
              >
                {selectedTags.map((tag) => (
                  <View
                    key={tag.tagId}
                    style={{
                      flexDirection: "row",
                      alignItems: "center",
                      backgroundColor: colors.primary,
                      paddingHorizontal: 12,
                      paddingVertical: 6,
                      borderRadius: 16,
                      marginRight: 8,
                      marginBottom: 8,
                    }}
                  >
                    <Text
                      style={{ color: "#fff", fontSize: 13, marginRight: 6 }}
                    >
                      {tag.name}
                    </Text>
                    <TouchableOpacity
                      onPress={() => handleRemoveTag(tag.tagId)}
                    >
                      <Text
                        style={{
                          color: "#fff",
                          fontSize: 16,
                          fontWeight: "bold",
                        }}
                      >
                        ×
                      </Text>
                    </TouchableOpacity>
                  </View>
                ))}
              </View>
            )}

            {/* Search Tags */}
            <View
              style={{
                borderWidth: 1,
                borderColor: "#E2E8F0",
                borderRadius: radii.md,
                paddingHorizontal: 12,
                paddingVertical: 14,
                backgroundColor: "#fff",
                marginBottom: 8,
              }}
            >
              <TextInput
                placeholder="Tìm kiếm hoặc tạo tag mới..."
                value={tagSearchQuery}
                onChangeText={setTagSearchQuery}
                editable={!loading}
              />
            </View>

            {tagSearching && (
              <ActivityIndicator style={{ marginBottom: 8 }} size="small" />
            )}

            {/* Tag Search Results */}
            {tagSearchResults.length > 0 && (
              <View
                style={{
                  borderWidth: 1,
                  borderColor: "#E2E8F0",
                  borderRadius: radii.md,
                  backgroundColor: "#fff",
                  marginBottom: 8,
                  maxHeight: 150,
                }}
              >
                <ScrollView>
                  {tagSearchResults.map((tag) => (
                    <TouchableOpacity
                      key={tag.tagId}
                      style={{
                        padding: 12,
                        borderBottomWidth: 1,
                        borderBottomColor: "#F1F5F9",
                      }}
                      onPress={() => handleSelectTag(tag)}
                    >
                      <Text style={{ fontSize: 14 }}>{tag.name}</Text>
                    </TouchableOpacity>
                  ))}
                </ScrollView>
              </View>
            )}

            {/* Create New Tag Button */}
            {tagSearchQuery.trim() && (
              <TouchableOpacity
                style={{
                  borderWidth: 1,
                  borderColor: colors.primary,
                  borderRadius: radii.md,
                  paddingVertical: 10,
                  paddingHorizontal: 12,
                  marginBottom: 8,
                }}
                onPress={handleCreateNewTag}
              >
                <Text
                  style={{
                    color: colors.primary,
                    textAlign: "center",
                    fontWeight: "600",
                  }}
                >
                  + Tạo tag mới "{tagSearchQuery}"
                </Text>
              </TouchableOpacity>
            )}
          </View>

          <View style={{ marginTop: 20, marginBottom: 40 }}>
            <TouchableOpacity
              style={{
                backgroundColor: colors.primary,
                paddingVertical: 16,
                borderRadius: radii.md,
                marginBottom: 12,
              }}
              onPress={handleSave}
              disabled={loading}
            >
              {loading ? (
                <ActivityIndicator color="#fff" />
              ) : (
                <Text
                  style={{
                    color: "#fff",
                    textAlign: "center",
                    fontWeight: "800",
                    fontSize: 16,
                  }}
                >
                  Lưu thay đổi
                </Text>
              )}
            </TouchableOpacity>

            <TouchableOpacity
              style={{
                backgroundColor: "#E2E8F0",
                paddingVertical: 16,
                borderRadius: radii.md,
              }}
              onPress={() => navigation.goBack()}
              disabled={loading}
            >
              <Text
                style={{
                  color: colors.text,
                  textAlign: "center",
                  fontWeight: "800",
                  fontSize: 16,
                }}
              >
                Hủy
              </Text>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
