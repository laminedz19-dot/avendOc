import { StyleSheet, Text, TextInput, type TextInputProps, View } from "react-native";
import { colors } from "@/lib/ui";

export function FormField({ label, ...props }: TextInputProps & { label: string }) {
  return <View style={styles.wrap}><Text style={styles.label}>{label}</Text><TextInput placeholderTextColor="#98A8A1" style={styles.input} {...props} /></View>;
}

const styles = StyleSheet.create({
  wrap: { gap: 6 },
  label: { fontSize: 13, fontWeight: "700", color: colors.text },
  input: { minHeight: 48, borderWidth: 1, borderColor: colors.border, borderRadius: 12, paddingHorizontal: 13, color: colors.text, backgroundColor: colors.surface, textAlign: "right" },
});
