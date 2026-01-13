import axios from "axios";
import Constants from "expo-constants";
import * as SecureStore from "expo-secure-store";
import { Platform } from "react-native";

const API_BASE_URL =
  Constants.expoConfig?.extra?.API_BASE_URL ||
  process.env.API_BASE_URL ||
  "http://localhost:8080";

const client = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // 30 seconds timeout
});

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

// Global flag to prevent multiple logout calls
let isLoggingOut = false;

// Function to clear auth and redirect to login
const handleUnauthorized = async () => {
  if (isLoggingOut) return;
  isLoggingOut = true;

  try {
    // Clear token from storage
    if (Platform.OS === 'web') {
      localStorage.removeItem("auth_token");
    } else {
      await SecureStore.deleteItemAsync("auth_token");
    }

    // Notify user (optional - can be handled by AuthContext)
    console.warn("Session expired. Please login again.");

    // Note: Actual navigation to login should be handled by AuthContext
    // listening to auth state changes
  } catch (error) {
    console.error("Error during logout:", error);
  } finally {
    isLoggingOut = false;
  }
};

// Response interceptor for error handling and token refresh
client.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Handle 401 Unauthorized
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      // TODO: Implement token refresh logic here if backend supports it
      // For now, just logout user on 401
      await handleUnauthorized();

      return Promise.reject(error);
    }

    // Log other errors (but not in production)
    if (__DEV__) {
      console.error("API Error:", {
        url: originalRequest?.url,
        status: error.response?.status,
        message: error.response?.data?.message,
        data: error.response?.data,
      });
    }

    return Promise.reject(error);
  }
);

export default client;
