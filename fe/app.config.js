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
      API_BASE_URL: process.env.API_BASE_URL || "http://localhost:8080",
      GOOGLE_CLIENT_ID: process.env.GOOGLE_CLIENT_ID,
      GOOGLE_REDIRECT_URI: process.env.GOOGLE_REDIRECT_URI,
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
