import { useEffect, useMemo, useState } from "react";
import { ActivityIndicator, FlatList, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { useRouter } from "expo-router";
import { ScreenContainer } from "@/components/screen-container";
import { ListingCard } from "@/components/listing-card";
import { useAuth } from "@/hooks/use-auth";
import { colors } from "@/lib/ui";
import { listenPublishedListings } from "@/lib/marketplace";
import { CATEGORIES, type Listing } from "@/types/marketplace";

export default function MarketScreen() {
  const router = useRouter();
  const { user } = useAuth();
  const [listings, setListings] = useState<Listing[]>([]);
  const [queryText, setQueryText] = useState("");
  const [category, setCategory] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => listenPublishedListings((items) => { setListings(items); setError(""); setLoading(false); }, (nextError) => { setError(nextError.message); setLoading(false); }), []);

  const filtered = useMemo(() => listings.filter((item) => {
    const haystack = `${item.title} ${item.description} ${item.wilayaNameAr} ${item.wilayaNameFr}`.toLowerCase();
    return (!queryText.trim() || haystack.includes(queryText.trim().toLowerCase())) && (!category || item.category === category);
  }), [listings, queryText, category]);

  return (
    <ScreenContainer edges={["top", "left", "right"]} className="px-4">
      <View style={styles.header}><View><Text style={styles.brand}>avendOc</Text><Text style={styles.subtitle}>سوقك الجزائري بثقة</Text></View><Pressable style={styles.accountButton} onPress={() => router.push((user ? "/(tabs)/account" : "/auth") as never)}><Text style={styles.accountText}>{user ? "حسابي" : "دخول"}</Text></Pressable></View>
      <View style={styles.hero}><Text style={styles.heroTitle}>اكتشف ما يناسبك</Text><Text style={styles.heroText}>إعلانات موثقة بعد مراجعة الإدارة</Text></View>
      <TextInput value={queryText} onChangeText={setQueryText} placeholder="ابحث عن سيارة، هاتف، عقار..." placeholderTextColor="#94A49D" style={styles.search} returnKeyType="search" />
      <FlatList horizontal showsHorizontalScrollIndicator={false} data={CATEGORIES} keyExtractor={(item) => item.id} contentContainerStyle={styles.chips} renderItem={({ item }) => <Pressable onPress={() => setCategory(category === item.id ? null : item.id)} style={[styles.chip, category === item.id && styles.chipActive]}><Text style={[styles.chipText, category === item.id && styles.chipTextActive]}>{item.ar}</Text></Pressable>} />
      <View style={styles.sectionHeader}><Text style={styles.sectionTitle}>أحدث الإعلانات</Text><Text style={styles.count}>{filtered.length} إعلان</Text></View>
      {loading ? <View style={styles.center}><ActivityIndicator color={colors.primary} /><Text style={styles.muted}>جارٍ تحميل الإعلانات...</Text></View> : error ? <View style={styles.empty}><Text style={styles.emptyTitle}>تعذر تحميل الإعلانات</Text><Text style={styles.muted}>{error}</Text><Text style={styles.muted}>تحقق من Firebase Authentication وقواعد Firestore.</Text></View> : <FlatList data={filtered} keyExtractor={(item) => item.id} renderItem={({ item }) => <ListingCard listing={item} onPress={() => router.push((`/listing/${item.id}`) as never)} />} ListEmptyComponent={<View style={styles.empty}><Text style={styles.emptyTitle}>لا توجد نتائج</Text><Text style={styles.muted}>جرّب كلمة بحث أو تصنيفًا آخر.</Text></View>} contentContainerStyle={filtered.length ? undefined : styles.emptyList} />}
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({ header: { flexDirection: "row-reverse", justifyContent: "space-between", alignItems: "center", paddingTop: 8, paddingBottom: 18 }, brand: { color: colors.primary, fontSize: 28, fontWeight: "900", textAlign: "right" }, subtitle: { color: colors.muted, fontSize: 12, textAlign: "right", marginTop: 2 }, accountButton: { backgroundColor: "#E5F3EE", paddingHorizontal: 16, paddingVertical: 10, borderRadius: 12 }, accountText: { color: colors.primaryDark, fontWeight: "800" }, hero: { backgroundColor: colors.primary, borderRadius: 20, padding: 20, marginBottom: 14 }, heroTitle: { color: "white", fontSize: 22, fontWeight: "900", textAlign: "right" }, heroText: { color: "#D5F4E6", fontSize: 13, textAlign: "right", marginTop: 7 }, search: { borderColor: colors.border, borderWidth: 1, borderRadius: 14, height: 50, backgroundColor: colors.surface, paddingHorizontal: 15, color: colors.text, textAlign: "right", marginBottom: 10 }, chips: { gap: 8, paddingVertical: 4 }, chip: { backgroundColor: colors.surface, borderColor: colors.border, borderWidth: 1, paddingHorizontal: 12, paddingVertical: 9, borderRadius: 20 }, chipActive: { backgroundColor: colors.primary, borderColor: colors.primary }, chipText: { color: colors.text, fontSize: 12, fontWeight: "700" }, chipTextActive: { color: "white" }, sectionHeader: { flexDirection: "row-reverse", alignItems: "center", justifyContent: "space-between", marginVertical: 14 }, sectionTitle: { color: colors.text, fontSize: 18, fontWeight: "900" }, count: { color: colors.muted, fontSize: 12 }, center: { flex: 1, alignItems: "center", justifyContent: "center", gap: 10 }, muted: { color: colors.muted }, empty: { alignItems: "center", gap: 8 }, emptyList: { flexGrow: 1, justifyContent: "center" }, emptyTitle: { fontSize: 18, fontWeight: "800", color: colors.text } });
