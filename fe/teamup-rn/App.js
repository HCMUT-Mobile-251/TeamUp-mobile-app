import React, { useEffect, useMemo, useState } from "react";
import {
  ActivityIndicator,
  View,
  Platform,
  TouchableOpacity,
  Text,
} from "react-native";
import * as SecureStore from "expo-secure-store";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import Constants from "expo-constants";
import LoginScreen from "./screens/LoginScreen";
import HomeScreen from "./screens/HomeScreen";
import SearchScreen from "./screens/SearchScreen";
import AdvancedSearchScreen from "./screens/AdvancedSearchScreen";
import NotificationsScreen from "./screens/NotificationsScreen";
import ProfileScreen from "./screens/ProfileScreen";
import JoinGroupScreen from "./screens/JoinGroupScreen";
import CreateGroupScreen from "./screens/CreateGroupScreen";
import GroupInfoScreen from "./screens/GroupInfoScreen";
import Onboarding1Screen from "./screens/Onboarding1Screen";
import Onboarding2Screen from "./screens/Onboarding2Screen";

export const AuthContext = React.createContext();

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

import { Ionicons } from "@expo/vector-icons";
import { LinearGradient } from "expo-linear-gradient";

function Tabs({ navigation }) {
  // Nút + ở giữa
  const PlusButton = ({ onPress }) => (
    <TouchableOpacity
      activeOpacity={0.9}
      onPress={onPress}
      style={{
        top: -24,
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <LinearGradient
        colors={["#2B5CFF", "#6D8CFF"]}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        style={{
          width: 64,
          height: 64,
          borderRadius: 32,
          justifyContent: "center",
          alignItems: "center",
          shadowColor: "#000",
          shadowOpacity: 0.18,
          shadowRadius: 16,
          shadowOffset: { width: 0, height: 10 },
          elevation: 6,
        }}
      >
        <Ionicons name="add" size={28} color="#fff" />
      </LinearGradient>
    </TouchableOpacity>
  );

  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarShowLabel: true,
        tabBarActiveTintColor: "#2B5CFF",
        tabBarStyle: {
          position: "absolute",
          left: 16,
          right: 16,
          bottom: 16,
          height: 64,
          borderRadius: 20,
          backgroundColor: "#fff",
          shadowColor: "#000",
          shadowOpacity: 0.08,
          shadowRadius: 12,
          elevation: 4,
          paddingBottom: Platform.OS === "ios" ? 8 : 6,
        },
        tabBarIcon: ({ focused, color, size }) => {
          const map = {
            Home: "home",
            Search: "search",
            Notifications: "notifications",
            Profile: "person",
          };
          const icon = map[route.name];
          return icon ? (
            <Ionicons
              name={focused ? icon : `${icon}-outline`}
              size={22}
              color={color}
            />
          ) : null;
        },
        tabBarLabelStyle: { fontSize: 12 },
      })}
    >
      <Tab.Screen name="Home" component={HomeScreen} />
      <Tab.Screen name="Search" component={SearchScreen} />

      {/* Tab giữa — chỉ là nút action, không có screen riêng */}
      <Tab.Screen
        name="Add"
        component={View} // dummy
        options={{
          tabBarLabel: "",
          tabBarIcon: () => null,
          tabBarButton: (props) => (
            <PlusButton onPress={() => navigation.navigate("CreateGroup")} />
          ),
        }}
        listeners={{
          tabPress: (e) => {
            // chặn focus tab mặc định
            e.preventDefault();
          },
        }}
      />

      <Tab.Screen name="Notifications" component={NotificationsScreen} />
      <Tab.Screen name="Profile" component={ProfileScreen} />
    </Tab.Navigator>
  );
}

export default function App() {
  const [loading, setLoading] = useState(true);
  const [token, setToken] = useState(null);
  const [hasSeenOnboarding, setHasSeenOnboarding] = useState(false);

  // 👉 đọc flag SKIP_AUTH từ app.json
  const SKIP_AUTH = Constants.expoConfig?.extra?.SKIP_AUTH;

  useEffect(() => {
    (async () => {
      if (SKIP_AUTH) {
        // Bỏ qua đăng nhập – gán token giả để app vào thẳng Home
        setToken("debug-token");
        setLoading(false);
      } else {
        const t = await SecureStore.getItemAsync("auth_token");
        const onboardingSeen = await SecureStore.getItemAsync("onboarding_seen");
        setToken(t);
        setHasSeenOnboarding(onboardingSeen === "true");
        setLoading(false);
      }
    })();
  }, []);

  const authContext = useMemo(
    () => ({
      signIn: async (tkn) => {
        await SecureStore.setItemAsync("auth_token", tkn);
        setToken(tkn);
      },
      signOut: async () => {
        await SecureStore.deleteItemAsync("auth_token");
        setToken(null);
      },
      markOnboardingSeen: async () => {
        await SecureStore.setItemAsync("onboarding_seen", "true");
        setHasSeenOnboarding(true);
      },
      resetOnboarding: async () => {
        await SecureStore.deleteItemAsync("onboarding_seen");
        setHasSeenOnboarding(false);
      },
      token,
      hasSeenOnboarding,
    }),
    [token, hasSeenOnboarding]
  );

  if (loading) {
    return (
      <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
        <ActivityIndicator />
      </View>
    );
  }

  return (
    <AuthContext.Provider value={authContext}>
      <NavigationContainer>
        <Stack.Navigator>
          {token ? (
            <>
              <Stack.Screen
                name="Tabs"
                component={Tabs}
                options={{ headerShown: false }}
              />
              <Stack.Screen
                name="JoinGroup"
                component={JoinGroupScreen}
                options={{ title: "Tham Gia nhóm" }}
              />
              <Stack.Screen
                name="CreateGroup"
                component={CreateGroupScreen}
                options={{ title: "Tạo nhóm" }}
              />
              <Stack.Screen
                name="GroupInfo"
                component={GroupInfoScreen}
                options={{ title: "Thông tin nhóm" }}
              />
              <Stack.Screen
                name="AdvancedSearch"
                component={AdvancedSearchScreen}
                options={{ title: "Tìm kiếm nâng cao" }}
              />
              <Stack.Screen
                name="Onboarding1"
                component={Onboarding1Screen}
                options={{ headerShown: false }}
              />
              <Stack.Screen
                name="Onboarding2"
                component={Onboarding2Screen}
                options={{ headerShown: false }}
              />
            </>
          ) : !hasSeenOnboarding ? (
            <>
              <Stack.Screen
                name="Onboarding1"
                component={Onboarding1Screen}
                options={{ headerShown: false }}
              />
              <Stack.Screen
                name="Onboarding2"
                component={Onboarding2Screen}
                options={{ headerShown: false }}
              />
              <Stack.Screen
                name="Login"
                component={LoginScreen}
                options={{ headerShown: false }}
              />
            </>
          ) : (
            <Stack.Screen
              name="Login"
              component={LoginScreen}
              options={{ headerShown: false }}
            />
          )}
        </Stack.Navigator>
      </NavigationContainer>
    </AuthContext.Provider>
  );
}
