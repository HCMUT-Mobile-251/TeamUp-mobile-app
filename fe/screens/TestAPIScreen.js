import React, { useState } from "react";
import { View, Text, Button, ScrollView, StyleSheet } from "react-native";
import { searchCourses } from "../src/api/courseService";
import { getAllGroups } from "../src/api/groupService";

export default function TestAPIScreen() {
  const [result, setResult] = useState("");
  const [loading, setLoading] = useState(false);

  const testCourseAPI = async () => {
    setLoading(true);
    try {
      const data = await searchCourses("vlkt");
      setResult(JSON.stringify(data, null, 2));
    } catch (error) {
      setResult(`Error: ${error.message}\n${JSON.stringify(error.response?.data, null, 2)}`);
    }
    setLoading(false);
  };

  const testGroupAPI = async () => {
    setLoading(true);
    try {
      const data = await getAllGroups();
      setResult(JSON.stringify(data, null, 2));
    } catch (error) {
      setResult(`Error: ${error.message}\n${JSON.stringify(error.response?.data, null, 2)}`);
    }
    setLoading(false);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Test API Connection</Text>

      <View style={styles.buttonContainer}>
        <Button
          title="Test Course API"
          onPress={testCourseAPI}
          disabled={loading}
        />
        <Button
          title="Test Group API"
          onPress={testGroupAPI}
          disabled={loading}
        />
      </View>

      <ScrollView style={styles.resultContainer}>
        <Text style={styles.resultText}>
          {loading ? "Loading..." : result || "Press a button to test API"}
        </Text>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: "#fff",
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 20,
  },
  buttonContainer: {
    flexDirection: "row",
    justifyContent: "space-around",
    marginBottom: 20,
  },
  resultContainer: {
    flex: 1,
    backgroundColor: "#f5f5f5",
    padding: 10,
    borderRadius: 5,
  },
  resultText: {
    fontFamily: "monospace",
    fontSize: 12,
  },
});
