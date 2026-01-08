import React, { useState, useEffect, useContext } from "react";
import { View, Text, TextInput, ScrollView, TouchableOpacity, ActivityIndicator } from "react-native";
import Screen from "../src/ui/Screen";
import Tag from "../src/components/Tag";
import ProjectCard from "../src/components/ProjectCard";
import { colors, radii } from "../src/ui/theme";
import { searchNormal } from "../src/api/searchService";
import { getAllTags } from "../src/api/tagService";
import { getSuggestedGroups } from "../src/api/groupService";
import { AuthContext } from "../App";

export default function SearchScreen({ navigation }) {
  const { userId } = useContext(AuthContext);
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [suggestedGroups, setSuggestedGroups] = useState([]);
  const [tags, setTags] = useState([]);
  const [loading, setLoading] = useState(false);
  const [tagsLoading, setTagsLoading] = useState(true);
  const [suggestionsLoading, setSuggestionsLoading] = useState(true);

  // Load popular tags and suggested groups on mount
  useEffect(() => {
    loadTags();
    loadSuggestedGroups();
  }, [userId]);

  const loadTags = async () => {
    try {
      const response = await getAllTags();
      console.log("[SearchScreen] getAllTags response:", response);
      if (response.code === 200) {
        console.log("[SearchScreen] Tags loaded:", response.result?.length || 0, "tags");
        setTags(response.result || []);
      } else {
        console.log("[SearchScreen] Unexpected response code:", response.code);
        setTags([]);
      }
    } catch (error) {
      console.error("[SearchScreen] Error loading tags:", error);
      console.error("[SearchScreen] Error details:", error.response?.data);
      setTags([]);
    } finally {
      setTagsLoading(false);
    }
  };

  const loadSuggestedGroups = async () => {
    if (!userId) return;

    try {
      const response = await getSuggestedGroups(userId, 0, 10);
      console.log("[SearchScreen] getSuggestedGroups response:", response);
      if (response.code === 200) {
        console.log("[SearchScreen] Suggested groups loaded:", response.result?.content?.length || 0, "groups");
        setSuggestedGroups(response.result?.content || []);
      } else {
        console.log("[SearchScreen] Unexpected response code:", response.code);
        setSuggestedGroups([]);
      }
    } catch (error) {
      console.error("[SearchScreen] Error loading suggested groups:", error);
      console.error("[SearchScreen] Error details:", error.response?.data);
      setSuggestedGroups([]);
    } finally {
      setSuggestionsLoading(false);
    }
  };

  const handleSearch = async (query) => {
    if (!query.trim()) {
      setSearchResults([]);
      return;
    }

    setLoading(true);
    try {
      const response = await searchNormal(query, userId);
      if (response.code === 200) {
        setSearchResults(response.result || []);
      }
    } catch (error) {
      console.error("Search error:", error);
      setSearchResults([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = (text) => {
    setSearchQuery(text);
    // Debounce search
    const timeoutId = setTimeout(() => {
      handleSearch(text);
    }, 500);
    return () => clearTimeout(timeoutId);
  };

  const handleTagPress = (tagName) => {
    setSearchQuery(tagName);
    handleSearch(tagName);
  };

  return (
    <Screen>
      <ScrollView showsVerticalScrollIndicator={false}>
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
          <TextInput
            placeholder="Tìm theo tên nhóm, tên đề tài, hoặc tag..."
            value={searchQuery}
            onChangeText={handleSearchChange}
            style={{ fontSize: 16 }}
          />
        </View>

        {/* Advanced Search Button */}
        <TouchableOpacity
          onPress={() => navigation.navigate("AdvancedSearch")}
          style={{ marginBottom: 20, alignSelf: "flex-end" }}
        >
          <Text style={{ color: colors.primary, fontWeight: "600" }}>
            Tìm kiếm nâng cao →
          </Text>
        </TouchableOpacity>

        {/* Tags Section */}
        <View style={{ marginBottom: 20 }}>
          <Text style={{ fontWeight: "800", marginBottom: 4 }}>Tags phổ biến</Text>
          <Text style={{ fontSize: 12, color: colors.subtext, marginBottom: 8 }}>
            Nhấn vào tag để tìm kiếm nhanh
          </Text>
          {tagsLoading ? (
            <ActivityIndicator style={{ marginVertical: 10 }} />
          ) : tags.length > 0 ? (
            <View style={{ flexDirection: "row", flexWrap: "wrap" }}>
              {tags.slice(0, 12).map((tag) => (
                <TouchableOpacity key={tag.tagId} onPress={() => handleTagPress(tag.name)}>
                  <Tag label={tag.name} />
                </TouchableOpacity>
              ))}
            </View>
          ) : (
            <Text style={{ fontSize: 13, color: colors.subtext, fontStyle: "italic" }}>
              Chưa có tags phổ biến
            </Text>
          )}
        </View>

        {/* Search Results */}
        {searchQuery.trim() ? (
          <>
            <Text style={{ marginTop: 8, fontWeight: "800", marginBottom: 8 }}>
              Kết quả tìm kiếm ({searchResults.length})
            </Text>
            {loading ? (
              <View style={{ padding: 20, alignItems: "center" }}>
                <ActivityIndicator size="large" color={colors.primary} />
              </View>
            ) : searchResults.length > 0 ? (
              searchResults.map((group) => (
                <ProjectCard
                  key={group.groupId}
                  data={group}
                  onPress={() =>
                    navigation.navigate("GroupInfo", { groupId: group.groupId })
                  }
                />
              ))
            ) : (
              <View style={{ padding: 24, alignItems: "center" }}>
                <Text style={{ color: colors.subtext, fontSize: 16 }}>
                  Không tìm thấy kết quả phù hợp
                </Text>
              </View>
            )}
          </>
        ) : (
          <>
            <Text style={{ marginTop: 16, fontWeight: "800", marginBottom: 8 }}>
              Gợi ý cho bạn
            </Text>
            {suggestionsLoading ? (
              <View style={{ padding: 20, alignItems: "center" }}>
                <ActivityIndicator size="large" color={colors.primary} />
              </View>
            ) : suggestedGroups.length > 0 ? (
              suggestedGroups.map((group) => (
                <ProjectCard
                  key={group.groupId}
                  data={group}
                  onPress={() =>
                    navigation.navigate("GroupInfo", { groupId: group.groupId })
                  }
                />
              ))
            ) : (
              <View style={{ padding: 24, alignItems: "center" }}>
                <Text style={{ color: colors.subtext, fontSize: 16 }}>
                  Chưa có gợi ý phù hợp
                </Text>
                <Text style={{ color: colors.subtext, fontSize: 14, marginTop: 8, textAlign: "center" }}>
                  Nhập từ khóa để tìm kiếm nhóm
                </Text>
              </View>
            )}
          </>
        )}
      </ScrollView>
    </Screen>
  );
}