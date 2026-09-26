import { useContext, createContext, useEffect, useMemo, useState, type ReactNode } from "react";
import {
  createUserWithEmailAndPassword,
  onAuthStateChanged,
  signInWithEmailAndPassword,
  signOut,
  updateProfile,
  type User,
} from "firebase/auth";
import { doc, setDoc, serverTimestamp } from "firebase/firestore";
import { auth, db } from "@/lib/firebase";

export type AuthContextValue = {
  user: User | null;
  isAdmin: boolean;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string, wilayaCode: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshAdminClaim: () => Promise<boolean>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [loading, setLoading] = useState(true);

  const refreshAdminClaim = async () => {
    if (!auth.currentUser) { setIsAdmin(false); return false; }
    const token = await auth.currentUser.getIdTokenResult(true);
    const admin = token.claims.admin === true;
    setIsAdmin(admin);
    return admin;
  };

  useEffect(() => onAuthStateChanged(auth, async (nextUser) => {
    setUser(nextUser);
    if (nextUser) {
      try { await refreshAdminClaim(); } catch { setIsAdmin(false); }
    } else setIsAdmin(false);
    setLoading(false);
  }), []);

  const value = useMemo<AuthContextValue>(() => ({
    user,
    isAdmin,
    loading,
    login: async (email, password) => {
      await signInWithEmailAndPassword(auth, email.trim(), password);
      await refreshAdminClaim();
    },
    register: async (name, email, password, wilayaCode) => {
      const credentials = await createUserWithEmailAndPassword(auth, email.trim(), password);
      await updateProfile(credentials.user, { displayName: name.trim() });
      await setDoc(doc(db, "users", credentials.user.uid), {
        name: name.trim(), email: email.trim(), phone: "", wilayaCode, isBlocked: false, updatedAt: serverTimestamp(),
      });
      setUser(auth.currentUser);
      setIsAdmin(false);
    },
    logout: async () => { await signOut(auth); setIsAdmin(false); },
    refreshAdminClaim,
  }), [user, isAdmin, loading]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const value = useContext(AuthContext);
  if (!value) throw new Error("useAuth must be used inside AuthProvider");
  return value;
}
