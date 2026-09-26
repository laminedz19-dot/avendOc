import { useEffect, useState } from "react";
import { Alert, ActivityIndicator, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useLocalSearchParams, useRouter } from "expo-router";
import { ScreenContainer } from "@/components/screen-container";
import { FormField } from "@/components/form-field";
import { useAuth } from "@/hooks/use-auth";
import { createOffer, getListing } from "@/lib/marketplace";
import { colors, formatDzd } from "@/lib/ui";
import type { Listing } from "@/types/marketplace";

export default function ListingDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const { user } = useAuth();
  const [listing, setListing] = useState<Listing | null>(null);
  const [offerPrice, setOfferPrice] = useState("");
  const [message, setMessage] = useState("");
  const [busy, setBusy] = useState(false);
  useEffect(() => { if (id) getListing(id).then(setListing); }, [id]);
  if (!listing) return <ScreenContainer edges={["top", "bottom", "left", "right"]}><View style={styles.center}><ActivityIndicator color={colors.primary} /><Text style={styles.muted}>جارٍ تحميل الإعلان...</Text></View></ScreenContainer>;
  const currentListing = listing;
  async function sendOffer() { if (!user) { router.push("/auth" as never); return; } const price = Number(offerPrice); if (!price || price <= 0) { Alert.alert("السعر ناقص", "أدخل قيمة العرض."); return; } setBusy(true); try { await createOffer({ listingId: currentListing.id, listingTitle: currentListing.title, sellerId: currentListing.sellerId, buyerId: user.uid, buyerName: user.displayName || user.email || "مشتري", buyerPhone: user.phoneNumber || "", originalPriceDzd: currentListing.priceDzd, proposedPriceDzd: price, message }); Alert.alert("تم إرسال العرض", "سيظهر العرض للبائع في تبويب العروض."); setOfferPrice(""); setMessage(""); } catch (error) { Alert.alert("تعذر إرسال العرض", error instanceof Error ? error.message : "حاول مرة أخرى."); } finally { setBusy(false); } }
  return <ScreenContainer edges={["top", "bottom", "left", "right"]} className="px-4"><ScrollView contentContainerStyle={styles.content}><Pressable onPress={() => router.back()}><Text style={styles.back}>‹ رجوع إلى السوق</Text></Pressable><View style={styles.cover}><Text style={styles.coverIcon}>▣</Text></View><Text style={styles.title}>{currentListing.title}</Text><Text style={styles.price}>{formatDzd(currentListing.priceDzd)}</Text><Text style={styles.meta}>{currentListing.wilayaNameAr} · {currentListing.commune} · {currentListing.condition}</Text><View style={styles.card}><Text style={styles.label}>الوصف</Text><Text style={styles.description}>{currentListing.description}</Text></View><View style={styles.card}><Text style={styles.label}>البائع</Text><Text style={styles.description}>{currentListing.sellerName || "بائع avendOc"}</Text><Text style={styles.meta}>{currentListing.sellerPhone}</Text></View>{currentListing.sellerId !== user?.uid && <View style={styles.card}><Text style={styles.label}>قدّم عرضًا</Text><FormField label="السعر المقترح بالدينار" value={offerPrice} onChangeText={setOfferPrice} keyboardType="number-pad" placeholder={String(currentListing.priceDzd)} /><FormField label="رسالة (اختياري)" value={message} onChangeText={setMessage} placeholder="هل السعر قابل للتفاوض؟" multiline /><Pressable onPress={sendOffer} disabled={busy} style={styles.primary}>{busy ? <ActivityIndicator color="white" /> : <Text style={styles.primaryText}>إرسال العرض</Text>}</Pressable></View>}</ScrollView></ScreenContainer>;
}

const styles = StyleSheet.create({ content: { gap: 14, paddingBottom: 30 }, back: { color: colors.primary, fontWeight: "800", textAlign: "right", paddingVertical: 8 }, cover: { height: 210, borderRadius: 20, backgroundColor: "#E5F3EE", alignItems: "center", justifyContent: "center" }, coverIcon: { color: colors.primary, fontSize: 60 }, title: { color: colors.text, fontSize: 27, fontWeight: "900", textAlign: "right" }, price: { color: colors.primary, fontSize: 21, fontWeight: "900", textAlign: "right" }, meta: { color: colors.muted, textAlign: "right" }, card: { backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border, borderRadius: 18, padding: 16, gap: 10 }, label: { color: colors.text, fontWeight: "900", fontSize: 16, textAlign: "right" }, description: { color: colors.text, lineHeight: 22, textAlign: "right" }, primary: { backgroundColor: colors.primary, minHeight: 50, alignItems: "center", justifyContent: "center", borderRadius: 12 }, primaryText: { color: "white", fontWeight: "800", fontSize: 16 }, center: { flex: 1, alignItems: "center", justifyContent: "center", gap: 10 }, muted: { color: colors.muted } });
