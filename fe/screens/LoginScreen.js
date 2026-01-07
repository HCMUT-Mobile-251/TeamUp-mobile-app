import React, { useContext, useState, useEffect } from "react";
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
import * as Linking from "expo-linking";
import axios from "axios";

// Cần thiết để đóng browser window sau khi auth hoàn thành
WebBrowser.maybeCompleteAuthSession();

export default function LoginScreen() {
  const { signIn, setUserId, markOnboardingSeen } = useContext(AuthContext);
  const [loading, setLoading] = useState(false);

  // Lấy config từ .env
  const API_BASE_URL = Constants?.expoConfig?.extra?.API_BASE_URL || "http://localhost:8080";
  const GOOGLE_CLIENT_ID = Constants?.expoConfig?.extra?.GOOGLE_CLIENT_ID;

  // Lắng nghe deep link từ auth-redirect.html
  useEffect(() => {
    const handleDeepLink = async (event) => {
      const url = event.url;
      console.log('[LoginScreen] Received deep link:', url);

      try {
        // Parse URL để lấy token và userId
        const { path, queryParams } = Linking.parse(url);

        console.log('[LoginScreen] Path:', path);
        console.log('[LoginScreen] Query params:', queryParams);

        // Kiểm tra xem có phải là auth callback không
        if (path === 'auth' || url.includes('auth')) {
          const { token, userId } = queryParams || {};

          console.log('[LoginScreen] Token from deep link:', token ? token.substring(0, 20) + '...' : 'null');
          console.log('[LoginScreen] UserId from deep link:', userId);

          if (token && userId) {
            setLoading(true);

            // Lưu userId và token
            await setUserId(userId);

            // Đánh dấu đã xem onboarding
            await markOnboardingSeen();

            // Sign in cuối cùng
            await signIn(token);

            console.log('[LoginScreen] Login successful via deep link');
          }
        }
      } catch (error) {
        console.error('[LoginScreen] Deep link error:', error);
        Alert.alert('Lỗi', 'Không thể xử lý đăng nhập');
      } finally {
        setLoading(false);
      }
    };

    // Đăng ký listener cho deep link
    const subscription = Linking.addEventListener('url', handleDeepLink);

    // Kiểm tra xem có deep link nào khi app mở lần đầu không
    Linking.getInitialURL().then((url) => {
      if (url) {
        console.log('[LoginScreen] Initial URL:', url);
        handleDeepLink({ url });
      }
    });

    // Cleanup
    return () => {
      subscription.remove();
    };
  }, [signIn, setUserId, markOnboardingSeen]);

  const handleGoogle = async () => {
    try {
      setLoading(true);

      // Tạo Expo redirect URI
      const redirectUri = Linking.createURL('auth');
      console.log('[LoginScreen] Expo redirect URI:', redirectUri);

      // Build Google OAuth URL với redirect URI được inject vào
      // Thêm state parameter để truyền redirectUri cho backend
      const state = encodeURIComponent(JSON.stringify({ redirectUri }));
      const authUrl = `https://accounts.google.com/o/oauth2/auth?scope=email profile openid&redirect_uri=${API_BASE_URL}/auth/login&response_type=code&client_id=${GOOGLE_CLIENT_ID}&state=${state}&approval_prompt=force`;

      console.log('[LoginScreen] Opening auth URL:', authUrl);

      // Mở Google OAuth trong in-app browser với auth session
      // Backend sẽ xử lý và redirect về auth-redirect.html
      // auth-redirect.html sẽ deep link về app với token và userId
      const result = await WebBrowser.openAuthSessionAsync(
        authUrl,
        redirectUri
      );

      console.log('[LoginScreen] Browser result:', result);

      // Xử lý kết quả
      if (result.type === 'success') {
        // Parse URL để lấy token và userId
        const { queryParams } = Linking.parse(result.url);
        const { token, userId } = queryParams || {};

        console.log('[LoginScreen] Token from result:', token ? token.substring(0, 20) + '...' : 'null');
        console.log('[LoginScreen] UserId from result:', userId);

        if (token && userId) {
          // Lưu userId và token
          await setUserId(userId);

          // Đánh dấu đã xem onboarding để tránh lỗi navigation
          await markOnboardingSeen();

          // Sign in cuối cùng để trigger re-render với authenticated state
          await signIn(token);

          console.log('[LoginScreen] Login successful');
        } else {
          throw new Error('Không nhận được token hoặc userId từ callback');
        }
      } else if (result.type === 'cancel') {
        console.log('[LoginScreen] User cancelled');
        setLoading(false);
      } else if (result.type === 'dismiss' || result.type === 'locked') {
        console.log('[LoginScreen] Browser dismissed or locked');
        setLoading(false);
      }

    } catch (error) {
      console.error('[LoginScreen] Login error:', error);
      Alert.alert('Lỗi', error.message || 'Đã xảy ra lỗi khi đăng nhập');
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
