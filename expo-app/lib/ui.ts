import { Alert, Platform } from "react-native";

export const colors = {
  primary: "#087F5B",
  primaryDark: "#065F46",
  accent: "#F59F00",
  background: "#F7FAF9",
  surface: "#FFFFFF",
  text: "#12312A",
  muted: "#6B7B75",
  border: "#D9E5E0",
  danger: "#C92A2A",
  success: "#2B8A3E",
};

export function formatDzd(value: number) {
  return `${Math.round(value).toLocaleString("fr-DZ")} دج`;
}

export function showError(error: unknown, fallback = "تعذر تنفيذ العملية") {
  const message = error instanceof Error ? error.message : fallback;
  Alert.alert("حدث خطأ", message || fallback);
}

export function haptic() {
  if (Platform.OS !== "web") return;
}
