

module.exports = {
  expo: {
    name: "Team up",
    slug: "teamup-rn",
    scheme: "teamup",
    version: "1.0.0",
    orientation: "portrait",
    icon: "./assets/icon.png",
    splash: {
      image: "./assets/splash.png",
      resizeMode: "contain",
      backgroundColor: "#ffffff"
    },
    extra: {
      API_BASE_URL: process.env.API_BASE_URL || "http://localhost:8080",
      GOOGLE_CLIENT_ID: process.env.GOOGLE_CLIENT_ID || "67346913521-0bql06om6o8kj610ferhl52le2uqh3jr.apps.googleusercontent.com",
      GOOGLE_REDIRECT_URI: process.env.GOOGLE_REDIRECT_URI || "http://localhost:8080/auth-redirect.html",
      SKIP_AUTH: process.env.SKIP_AUTH === "true" || false
    },
    ios: {
      supportsTablet: true
    },
    android: {
      adaptiveIcon: {
        foregroundImage: "./assets/adaptive-icon.png",
        backgroundColor: "#ffffff"
      }
    },
    web: {
      bundler: "metro"
    }
  }
};

