import axios from "axios";
import Constants from "expo-constants";
import * as SecureStore from "expo-secure-store";
import { Platform } from "react-native";

const API_BASE_URL =
  Constants.expoConfig?.extra?.API_BASE_URL ||
  process.env.API_BASE_URL ||
  "http://localhost:8080";

const client = axios.create({ baseURL: API_BASE_URL });

client.interceptors.request.use(async (config) => {
  let token = null;

  // Sử dụng SecureStore cho native platforms, localStorage cho web
  if (Platform.OS === 'web') {
    token = localStorage.getItem("auth_token");
  } else {
    token = await SecureStore.getItemAsync("auth_token");
  }

  if (token) {
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

// Thêm response interceptor để xử lý lỗi
client.interceptors.response.use(
  (response) => response,
  (error) => {
    // Log chi tiết lỗi
    console.error("API Error:", {
      status: error.response?.status,
      message: error.response?.data?.message,
      data: error.response?.data,
    });
    return Promise.reject(error);
  }
);

export default client;
