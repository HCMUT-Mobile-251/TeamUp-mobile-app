import React, { useContext, useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
  Platform,
} from "react-native";
import Constants from "expo-constants";
import { AuthContext } from "../App";
import * as WebBrowser from "expo-web-browser";
import * as AuthSession from "expo-auth-session";
import axios from "axios";

// Cần thiết để đóng browser window sau khi auth hoàn thành
WebBrowser.maybeCompleteAuthSession();

export default function LoginScreen() {
  const { signIn, setUserId } = useContext(AuthContext);
  const [loading, setLoading] = useState(false);

  // Lấy config từ .env
  const API_BASE_URL = Constants?.expoConfig?.extra?.API_BASE_URL || "http://localhost:8080";
  const GOOGLE_CLIENT_ID = Constants?.expoConfig?.extra?.GOOGLE_CLIENT_ID;

  // Tạo redirect URI cho Expo AuthSession
  const redirectUri = AuthSession.makeRedirectUri({
    scheme: 'teamup',
    path: 'auth'
  });

  console.log('[LoginScreen] Redirect URI:', redirectUri);

  const handleGoogle = async () => {
    try {
      setLoading(true);

      // Build Google OAuth URL
      const authUrl = `https://accounts.google.com/o/oauth2/auth?${new URLSearchParams({
        client_id: GOOGLE_CLIENT_ID,
        redirect_uri: redirectUri,
        response_type: 'code',
        scope: 'email profile openid',
        access_type: 'offline',
        prompt: 'consent'
      })}`;

      console.log('[LoginScreen] Opening auth URL:', authUrl);

      // Mở Google OAuth trong in-app browser
      const result = await WebBrowser.openAuthSessionAsync(
        authUrl,
        redirectUri
      );

      console.log('[LoginScreen] Auth result:', result);

      if (result.type === 'success') {
        // Parse URL để lấy code
        const url = result.url;
        const params = new URL(url).searchParams;
        const code = params.get('code');

        console.log('[LoginScreen] Received code:', code ? code.substring(0, 20) + '...' : 'null');

        if (!code) {
          throw new Error('Không nhận được authorization code từ Google');
        }

        // Gửi code cho backend để lấy token
        console.log('[LoginScreen] Sending code to backend:', `${API_BASE_URL}/auth/login`);
        const response = await axios.get(`${API_BASE_URL}/auth/login`, {
          params: { code }
        });

        console.log('[LoginScreen] Backend response:', response.data);

        if (response.data.code === 200) {
          const token = response.data.result.accessToken;
          const userId = response.data.result.user.userId;

          console.log('[LoginScreen] Login successful, userId:', userId);

          // Lưu userId và token
          await setUserId(userId);
          await signIn(token);
        } else {
          throw new Error(response.data.message || 'Đăng nhập thất bại');
        }
      } else if (result.type === 'cancel') {
        console.log('[LoginScreen] User cancelled login');
        Alert.alert('Thông báo', 'Bạn đã hủy đăng nhập');
      }
    } catch (error) {
      console.error('[LoginScreen] Login error:', error);
      Alert.alert('Lỗi', error.message || 'Đã xảy ra lỗi khi đăng nhập');
    } finally {
      setLoading(false);
    }
  };

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
        Đăng nhập bằng tài khoản HCMUT
      </Text>
    </View>
  );
}
