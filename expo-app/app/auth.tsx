import { useState } from "react";
import { ActivityIndicator, Alert, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useRouter } from "expo-router";
import { ScreenContainer } from "@/components/screen-container";
import { FormField } from "@/components/form-field";
import { useAuth } from "@/hooks/use-auth";
import { colors } from "@/lib/ui";

export default function AuthScreen() {
  const router = useRouter();
  const { login, register } = useAuth();
  const [mode, setMode] = useState<"login" | "register">("login");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [wilaya, setWilaya] = useState("16");
  const [busy, setBusy] = useState(false);

  async function submit() {
    if (!email.includes("@") || password.length < 6 || (mode === "register" && !name.trim())) { Alert.alert("بيانات ناقصة", "أدخل بريدًا صحيحًا وكلمة مرور من 6 أحرف على الأقل."); return; }
    setBusy(true);
    try { if (mode === "login") await login(email, password); else await register(name, email, password, wilaya); router.replace("/(tabs)"); } catch (error) { Alert.alert("تعذر الدخول", error instanceof Error ? error.message : "تحقق من البيانات وحاول مرة أخرى."); } finally { setBusy(false); }
  }

  return <ScreenContainer edges={["top", "bottom", "left", "right"]} className="px-5"><ScrollView contentContainerStyle={styles.content}><Pressable onPress={() => router.back()}><Text style={styles.back}>رجوع</Text></Pressable><View style={styles.logo}><Text style={styles.brand}>avendOc</Text><Text style={styles.subtitle}>سوقك الجزائري بثقة</Text></View><View style={styles.card}><Text style={styles.title}>{mode === "login" ? "مرحبًا بك" : "إنشاء حساب"}</Text><Text style={styles.hint}>{mode === "login" ? "سجّل الدخول لمتابعة عروضك وإعلاناتك." : "أنشئ حسابًا مجانيًا للشراء والبيع."}</Text>{mode === "register" && <FormField label="الاسم" value={name} onChangeText={setName} placeholder="اسمك الكامل" />}{mode === "register" && <FormField label="رمز الولاية" value={wilaya} onChangeText={setWilaya} keyboardType="number-pad" placeholder="16" />}<FormField label="البريد الإلكتروني" value={email} onChangeText={setEmail} autoCapitalize="none" keyboardType="email-address" placeholder="name@example.com" /><FormField label="كلمة المرور" value={password} onChangeText={setPassword} secureTextEntry placeholder="••••••••" /><Pressable onPress={submit} disabled={busy} style={({ pressed }) => [styles.primary, pressed && styles.pressed]}>{busy ? <ActivityIndicator color="white" /> : <Text style={styles.primaryText}>{mode === "login" ? "تسجيل الدخول" : "إنشاء الحساب"}</Text>}</Pressable><Pressable onPress={() => setMode(mode === "login" ? "register" : "login")}><Text style={styles.switch}>{mode === "login" ? "ليس لديك حساب؟ أنشئ حسابًا" : "لديك حساب؟ سجّل الدخول"}</Text></Pressable></View></ScrollView></ScreenContainer>;
}

const styles = StyleSheet.create({ content: { flexGrow: 1, paddingTop: 12, justifyContent: "center", gap: 18 }, back: { color: colors.primary, textAlign: "right", fontWeight: "800" }, logo: { alignItems: "center", marginVertical: 6 }, brand: { color: colors.primary, fontSize: 36, fontWeight: "900" }, subtitle: { color: colors.muted }, card: { backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, borderRadius: 22, padding: 20, gap: 14 }, title: { color: colors.text, fontSize: 25, fontWeight: "900", textAlign: "right" }, hint: { color: colors.muted, textAlign: "right", lineHeight: 20 }, primary: { minHeight: 50, borderRadius: 13, alignItems: "center", justifyContent: "center", backgroundColor: colors.primary, marginTop: 4 }, pressed: { opacity: 0.8 }, primaryText: { color: "white", fontSize: 16, fontWeight: "800" }, switch: { color: colors.primary, textAlign: "center", fontWeight: "800", paddingVertical: 6 } });
