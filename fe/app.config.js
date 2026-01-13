

module.exports = {
  expo: {
    name: "Team up",
    slug: "teamup-rn",
    scheme: "teamup",
    version: "1.0.0",
    orientation: "portrait",
    icon: "./assets/icon.png",
    plugins: [
      "expo-secure-store",
      "expo-web-browser"
    ],
    splash: {
      image: "./assets/splash-icon.png",
      resizeMode: "contain",
      backgroundColor: "#ffffff"
    },
    extra: {
      eas: {
        projectId: "e1a02a9b-e96e-4979-8f4d-0c5a58edbc6d"
      },
      API_BASE_URL: process.env.API_BASE_URL || "https://teamup-mobile-app.onrender.com", // Deployed backend
      GOOGLE_CLIENT_ID: process.env.GOOGLE_CLIENT_ID || "67346913521-0bql06om6o8kj610ferhl52le2uqh3jr.apps.googleusercontent.com",
      GOOGLE_REDIRECT_URI: process.env.GOOGLE_REDIRECT_URI || "https://teamup-mobile-app.onrender.com/auth/login",
      SKIP_AUTH: false
    },

    android: {
      package: "com.huihoang.teamup",  // ❗❗ BẮT BUỘC
      adaptiveIcon: {
        foregroundImage: "./assets/adaptive-icon.png",
        backgroundColor: "#ffffff"
      }
    },

    ios: {
      supportsTablet: true
    },

    web: {
      bundler: "metro"
    }
  }
};
