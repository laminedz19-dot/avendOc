import { Pressable, StyleSheet, Text, View } from "react-native";
import type { Listing } from "@/types/marketplace";
import { colors, formatDzd } from "@/lib/ui";

export function ListingCard({ listing, onPress }: { listing: Listing; onPress: () => void }) {
  return (
    <Pressable onPress={onPress} style={({ pressed }) => [styles.card, pressed && styles.pressed]}>
      <View style={styles.imagePlaceholder}><Text style={styles.imageIcon}>▣</Text></View>
      <View style={styles.body}>
        <View style={styles.rowBetween}><Text style={styles.title} numberOfLines={1}>{listing.title}</Text><Text style={styles.price}>{formatDzd(listing.priceDzd)}</Text></View>
        <Text style={styles.meta}>{listing.wilayaNameAr} · {listing.commune || "الجزائر"}</Text>
        <Text style={styles.description} numberOfLines={2}>{listing.description}</Text>
        <View style={styles.tags}><Text style={styles.tag}>{listing.isNegotiable ? "قابل للتفاوض" : "سعر ثابت"}</Text><Text style={styles.tag}>{listing.category}</Text></View>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: { flexDirection: "row", backgroundColor: colors.surface, borderRadius: 18, borderWidth: 1, borderColor: colors.border, marginBottom: 12, overflow: "hidden", minHeight: 132 },
  pressed: { opacity: 0.76, transform: [{ scale: 0.99 }] },
  imagePlaceholder: { width: 112, backgroundColor: "#E5F3EE", alignItems: "center", justifyContent: "center" },
  imageIcon: { fontSize: 32, color: colors.primary },
  body: { flex: 1, padding: 13, gap: 6 },
  rowBetween: { flexDirection: "row", alignItems: "center", gap: 8 },
  title: { flex: 1, fontSize: 16, fontWeight: "800", color: colors.text },
  price: { color: colors.primary, fontWeight: "800", fontSize: 14 },
  meta: { color: colors.muted, fontSize: 12 },
  description: { color: colors.text, fontSize: 13, lineHeight: 19 },
  tags: { flexDirection: "row", gap: 6 },
  tag: { color: colors.primaryDark, backgroundColor: "#E8F5EF", borderRadius: 8, paddingHorizontal: 8, paddingVertical: 4, fontSize: 11, overflow: "hidden" },
});
