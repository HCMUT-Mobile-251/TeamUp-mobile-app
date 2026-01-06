import React, { useState, useEffect, useContext } from "react";
import {
  View,
  Text,
  TextInput,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
} from "react-native";
import { colors, radii } from "../src/ui/theme";
import ProjectCard from "../src/components/ProjectCard";
import Tag from "../src/components/Tag";
import { searchAdvanced } from "../src/api/searchService";
import { getAllTags } from "../src/api/tagService";
import { searchCourses } from "../src/api/courseService";
import { AuthContext } from "../App";

export default function AdvancedSearchScreen({ navigation }) {
  const { userId } = useContext(AuthContext);
  const [searchCriteria, setSearchCriteria] = useState({
    name: "",
    groupClass: "",
    topicName: "",
    courseId: "",
    courseName: "",
    tagId: [],
  });

  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);

  const [tags, setTags] = useState([]);
  const [tagsLoading, setTagsLoading] = useState(true);
  const [courses, setCourses] = useState([]);
  const [courseSearching, setCourseSearching] = useState(false);

  // Load tags on mount
  useEffect(() => {
    loadTags();
  }, []);

  // Search courses when courseId changes
  useEffect(() => {
    if (searchCriteria.courseId.length >= 2) {
      searchCourse(searchCriteria.courseId);
    }
  }, [searchCriteria.courseId]);

  const loadTags = async () => {
    try {
      const response = await getAllTags();
      if (response.code === 200) {
        setTags(response.result || []);
      }
    } catch (error) {
      console.error("Error loading tags:", error);
    } finally {
      setTagsLoading(false);
    }
  };

  const searchCourse = async (query) => {
    setCourseSearching(true);
    try {
      const response = await searchCourses(query);
      if (response.code === 200 && response.result) {
        setCourses(response.result);
        const exactMatch = response.result.find(
          (c) => c.courseId.toLowerCase() === query.toLowerCase()
        );
        if (exactMatch) {
          setSearchCriteria((prev) => ({
            ...prev,
            courseName: exactMatch.name,
          }));
        }
      }
    } catch (error) {
      console.error("Course search error:", error);
    } finally {
      setCourseSearching(false);
    }
  };

  const handleInputChange = (field, value) => {
    setSearchCriteria((prev) => ({ ...prev, [field]: value }));
  };

  const toggleTag = (tagId) => {
    setSearchCriteria((prev) => {
      const currentTags = prev.tagId || [];
      const newTags = currentTags.includes(tagId)
        ? currentTags.filter((id) => id !== tagId)
        : [...currentTags, tagId];
      return { ...prev, tagId: newTags };
    });
  };

  const handleSearch = async () => {
    const criteria = { userId };

    if (searchCriteria.name.trim()) criteria.name = searchCriteria.name.trim();
    if (searchCriteria.groupClass.trim()) criteria.groupClass = searchCriteria.groupClass.trim();
    if (searchCriteria.topicName.trim()) criteria.topicName = searchCriteria.topicName.trim();
    if (searchCriteria.tagId && searchCriteria.tagId.length > 0) criteria.tagId = searchCriteria.tagId;
    if (searchCriteria.courseId.trim()) {
      criteria.course = {
        courseId: searchCriteria.courseId.trim(),
        name: searchCriteria.courseName.trim() || "",
      };
    }

    if (!criteria.name && !criteria.groupClass && !criteria.topicName && !criteria.course && (!criteria.tagId || criteria.tagId.length === 0)) {
      Alert.alert("Lỗi", "Vui lòng nhập ít nhất một tiêu chí tìm kiếm");
      return;
    }

    setLoading(true);
    setHasSearched(true);

    try {
      const response = await searchAdvanced(criteria);
      if (response.code === 200) {
        setSearchResults(response.result || []);
      } else {
        Alert.alert("Lỗi", response.message || "Không thể tìm kiếm");
        setSearchResults([]);
      }
    } catch (error) {
      console.error("Advanced search error:", error);
      Alert.alert("Lỗi", error.response?.data?.message || "Có lỗi xảy ra khi tìm kiếm");
      setSearchResults([]);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setSearchCriteria({
      name: "",
      groupClass: "",
      topicName: "",
      courseId: "",
      courseName: "",
      tagId: [],
    });
    setSearchResults([]);
    setHasSearched(false);
  };

  const renderInput = (label, field, placeholder) => (
    <View style={{ marginBottom: 12 }}>
      <Text style={{ marginBottom: 6, fontWeight: "600", color: colors.text }}>
        {label}
      </Text>
      <View
        style={{
          borderWidth: 1,
          borderColor: "#E2E8F0",
          borderRadius: radii.md,
          paddingHorizontal: 12,
          paddingVertical: 8,
          backgroundColor: colors.white,
        }}
      >
        <TextInput
          placeholder={placeholder}
          value={searchCriteria[field]}
          onChangeText={(value) => handleInputChange(field, value)}
          editable={!loading}
        />
      </View>
      {field === "courseId" && courseSearching && (
        <ActivityIndicator style={{ marginTop: 4 }} size="small" />
      )}
    </View>
  );

  return (
    <ScrollView contentContainerStyle={{ padding: 16 }} showsVerticalScrollIndicator={false}>
      <Text style={{ fontSize: 22, fontWeight: "900", marginBottom: 4 }}>
        Tìm kiếm nâng cao
      </Text>
      <Text style={{ fontSize: 14, color: colors.subtext, marginBottom: 16 }}>
        Sử dụng nhiều tiêu chí để tìm nhóm phù hợp
      </Text>

      {renderInput("Tên nhóm", "name", "VD: Nhóm 1")}
      {renderInput("Mã môn học", "courseId", "VD: CO3001")}
      {renderInput("Tên môn học", "courseName", "Sẽ tự động điền khi nhập mã môn")}
      {renderInput("Mã lớp", "groupClass", "VD: L01")}
      {renderInput("Tên đề tài", "topicName", "VD: Xây dựng ứng dụng...")}

      <Text style={{ marginBottom: 6, fontWeight: "600", color: colors.text, marginTop: 4 }}>
        Tags quan tâm
      </Text>
      {tagsLoading ? (
        <View style={{ padding: 20, alignItems: "center" }}>
          <ActivityIndicator size="small" />
        </View>
      ) : (
        <View style={{ flexDirection: "row", flexWrap: "wrap", marginBottom: 16 }}>
          {tags.slice(0, 12).map((tag) => (
            <TouchableOpacity key={tag.tagId} onPress={() => toggleTag(tag.tagId)}>
              <Tag label={tag.name} selected={searchCriteria.tagId && searchCriteria.tagId.includes(tag.tagId)} />
            </TouchableOpacity>
          ))}
        </View>
      )}

      <View style={{ flexDirection: "row", gap: 12, marginTop: 8 }}>
        <TouchableOpacity
          style={{
            flex: 1,
            backgroundColor: colors.white,
            paddingVertical: 16,
            borderRadius: radii.md,
            borderWidth: 1,
            borderColor: colors.subtext,
          }}
          onPress={handleClear}
          disabled={loading}
        >
          <Text style={{ color: colors.text, textAlign: "center", fontWeight: "700" }}>
            Xóa tất cả
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={{
            flex: 2,
            backgroundColor: loading ? colors.subtext : colors.primary,
            paddingVertical: 16,
            borderRadius: radii.md,
          }}
          onPress={handleSearch}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={{ color: "#fff", textAlign: "center", fontWeight: "800" }}>
              Tìm kiếm
            </Text>
          )}
        </TouchableOpacity>
      </View>

      {hasSearched && (
        <>
          <Text style={{ marginTop: 24, fontWeight: "800", fontSize: 18, marginBottom: 12 }}>
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
                onPress={() => navigation.navigate("GroupInfo", { groupId: group.groupId })}
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
      )}
    </ScrollView>
  );
}
