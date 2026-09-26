import { Tabs } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Platform, type ColorValue } from "react-native";
import { MaterialIcons } from "@expo/vector-icons";
import { useAuth } from "@/hooks/use-auth";
import { colors } from "@/lib/ui";

type TabIconProps = { name: keyof typeof MaterialIcons.glyphMap; color: string | ColorValue; size: number };
function TabIcon({ name, color, size }: TabIconProps) {
  return <MaterialIcons name={name} color={color} size={size} />;
}

export default function TabsLayout() {
  const insets = useSafeAreaInsets();
  const { isAdmin } = useAuth();
  const bottom = Platform.OS === "web" ? 10 : Math.max(insets.bottom, 8);

  return (
    <Tabs screenOptions={{ headerShown: false, tabBarActiveTintColor: colors.primary, tabBarInactiveTintColor: colors.muted, tabBarStyle: { height: 58 + bottom, paddingBottom: bottom, paddingTop: 6, backgroundColor: colors.surface, borderTopColor: colors.border } }}>
      <Tabs.Screen name="index" options={{ title: "السوق", tabBarIcon: (props) => <TabIcon name="storefront" {...props} /> }} />
      <Tabs.Screen name="create" options={{ title: "نشر إعلان", tabBarIcon: (props) => <TabIcon name="add-circle" {...props} /> }} />
      <Tabs.Screen name="offers" options={{ title: "العروض", tabBarIcon: (props) => <TabIcon name="handshake" {...props} /> }} />
      <Tabs.Screen name="account" options={{ title: "حسابي", tabBarIcon: (props) => <TabIcon name="person" {...props} /> }} />
      <Tabs.Screen name="admin" options={{ title: "الأدمين", href: isAdmin ? ("/(tabs)/admin" as never) : null, tabBarIcon: (props) => <TabIcon name="admin-panel-settings" {...props} /> }} />
    </Tabs>
  );
}
