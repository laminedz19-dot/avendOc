import type { ExpoConfig } from "expo/config";

const config: ExpoConfig = {
  name: "avendOc",
  slug: "avendoc-expo",
  version: "1.0.0",
  orientation: "portrait",
  icon: "./assets/images/icon.png",
  scheme: "avendoc",
  userInterfaceStyle: "light",
  newArchEnabled: true,
  ios: { supportsTablet: true, bundleIdentifier: "com.avendoc.expo", infoPlist: { ITSAppUsesNonExemptEncryption: false } },
  android: {
    package: "com.avendoc.expo",
    adaptiveIcon: { backgroundColor: "#E5F3EE", foregroundImage: "./assets/images/android-icon-foreground.png", backgroundImage: "./assets/images/android-icon-background.png", monochromeImage: "./assets/images/android-icon-monochrome.png" },
    edgeToEdgeEnabled: true,
    permissions: ["POST_NOTIFICATIONS"],
  },
  web: { bundler: "metro", output: "static", favicon: "./assets/images/favicon.png" },
  plugins: ["expo-router", ["expo-splash-screen", { image: "./assets/images/splash-icon.png", imageWidth: 200, resizeMode: "contain", backgroundColor: "#F7FAF9" }]],
  experiments: { typedRoutes: true, reactCompiler: true },
};

export default config;
