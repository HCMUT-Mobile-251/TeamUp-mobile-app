import React, { useContext, useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
} from "react-native";
import Constants from "expo-constants";
import { AuthContext } from "../App";
import * as Linking from "expo-linking";

export default function LoginScreen() {
  const { signIn } = useContext(AuthContext);
  const [loading, setLoading] = useState(false);

  const OAUTH_URL =
    Constants?.expoConfig?.extra?.GOOGLE_OAUTH_URL ||
    process.env.GOOGLE_OAUTH_URL ||
    "https://accounts.google.com/o/oauth2/auth?scope=email profile openid&redirect_uri=http://localhost:8080/auth/login&response_type=code&client_id=67346913521-0bql06om6o8kj610ferhl52le2uqh3jr.apps.googleusercontent.com&approval_prompt=force";

  const handleGoogle = async () => {
    try {
      setLoading(true);
      // Open external auth in browser; backend should complete Google flow then show a page with token in URL fragment or query (?token=...)
      const result = await Linking.openURL(OAUTH_URL);
    } catch (e) {
      Alert.alert("Lỗi", "Không mở được trang đăng nhập.");
    } finally {
      setLoading(false);
    }
  };

  // Deep link handler (teamup://auth?token=...)
  React.useEffect(() => {
    const sub = Linking.addEventListener("url", ({ url }) => {
      const parsed = Linking.parse(url);
      if (parsed?.queryParams?.token) {
        signIn(parsed.queryParams.token);
      }
    });
    return () => sub.remove();
  }, []);

  return (
    <View
      style={{
        flex: 1,
        alignItems: "center",
        justifyContent: "center",
        padding: 24,
      }}
    >
      <Text
        style={{
          fontSize: 36,
          color: "#2b5cff",
          marginBottom: 24,
          fontWeight: "800",
        }}
      >
        Team up
      </Text>
      <TouchableOpacity
        disabled={loading}
        onPress={handleGoogle}
        style={{
          flexDirection: "row",
          alignItems: "center",
          paddingHorizontal: 18,
          paddingVertical: 12,
          borderRadius: 28,
          borderWidth: 1,
          borderColor: "#ddd",
        }}
      >
        {loading ? <ActivityIndicator /> : <Text>Sign in with Google</Text>}
      </TouchableOpacity>
      <Text style={{ marginTop: 16, color: "#666", textAlign: "center" }}>
        Sau khi đăng nhập, BE hãy redirect về teamup://auth?token=JWT
      </Text>
    </View>
  );
}
