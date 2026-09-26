import {
  addDoc,
  collection,
  doc,
  getDoc,
  limit,
  onSnapshot,
  orderBy,
  query,
  serverTimestamp,
  updateDoc,
  where,
  type DocumentData,
  type Unsubscribe,
} from "firebase/firestore";
import { db } from "./firebase";
import type { AdminUser, Listing, MarketplaceUser, Offer } from "@/types/marketplace";

function asDateString(value: unknown) {
  if (typeof value === "string") return value;
  if (value && typeof value === "object" && "toDate" in value && typeof value.toDate === "function") {
    return value.toDate().toISOString();
  }
  return "";
}

function mapListing(id: string, data: DocumentData): Listing {
  return {
    id,
    title: data.title ?? "بدون عنوان",
    description: data.description ?? "",
    priceDzd: Number(data.priceDzd ?? 0),
    isNegotiable: Boolean(data.isNegotiable),
    category: data.category ?? "other",
    wilayaCode: data.wilayaCode ?? "16",
    wilayaNameAr: data.wilayaNameAr ?? "الجزائر",
    wilayaNameFr: data.wilayaNameFr ?? "Alger",
    commune: data.commune ?? "",
    condition: data.condition ?? "USED",
    sellerId: data.sellerId ?? "",
    sellerName: data.sellerName ?? "",
    sellerPhone: data.sellerPhone ?? "",
    status: data.status ?? "PAYMENT_PENDING",
    paymentReference: data.paymentReference ?? "",
    paymentDate: data.paymentDate ?? "",
    paymentProofReceiptUrl: data.paymentProofReceiptUrl ?? "",
    images: Array.isArray(data.images) ? data.images : [],
    deliveryOption: data.deliveryOption ?? "ALL_69_WILAYAS",
    createdAt: asDateString(data.createdAt),
    viewsCount: Number(data.viewsCount ?? 0),
    offersCount: Number(data.offersCount ?? 0),
    rejectionReason: data.rejectionReason ?? "",
  };
}

function mapOffer(id: string, data: DocumentData): Offer {
  return {
    id,
    listingId: data.listingId ?? "",
    listingTitle: data.listingTitle ?? "إعلان",
    sellerId: data.sellerId ?? "",
    buyerId: data.buyerId ?? "",
    buyerName: data.buyerName ?? "",
    buyerPhone: data.buyerPhone ?? "",
    originalPriceDzd: Number(data.originalPriceDzd ?? data.proposedPriceDzd ?? 0),
    proposedPriceDzd: Number(data.proposedPriceDzd ?? 0),
    counterPriceDzd: data.counterPriceDzd == null ? null : Number(data.counterPriceDzd),
    status: data.status ?? "PENDING",
    message: data.message ?? "",
    timestamp: asDateString(data.timestamp ?? data.updatedAt),
  };
}

export function listenPublishedListings(callback: (items: Listing[]) => void, onError?: (error: Error) => void): Unsubscribe {
  const q = query(collection(db, "listings"), where("status", "==", "PUBLISHED"), limit(100));
  return onSnapshot(q, (snapshot) => callback(snapshot.docs.map((item) => mapListing(item.id, item.data()))), onError);
}

export function listenMyListings(uid: string, callback: (items: Listing[]) => void): Unsubscribe {
  const q = query(collection(db, "listings"), where("sellerId", "==", uid), limit(100));
  return onSnapshot(q, (snapshot) => callback(snapshot.docs.map((item) => mapListing(item.id, item.data()))));
}

export function listenPendingListings(callback: (items: Listing[]) => void, onError: (error: Error) => void): Unsubscribe {
  const q = query(collection(db, "listings"), where("status", "==", "PAYMENT_PENDING"), limit(100));
  return onSnapshot(q, (snapshot) => callback(snapshot.docs.map((item) => mapListing(item.id, item.data()))), onError);
}

export async function getListing(id: string) {
  const snapshot = await getDoc(doc(db, "listings", id));
  return snapshot.exists() ? mapListing(snapshot.id, snapshot.data()) : null;
}

export async function createListing(input: Omit<Listing, "id" | "status" | "createdAt">) {
  const reference = await addDoc(collection(db, "listings"), {
    ...input,
    status: "PAYMENT_PENDING",
    createdAt: serverTimestamp(),
    viewsCount: 0,
    offersCount: 0,
  });
  return reference.id;
}

export async function createOffer(input: Omit<Offer, "id" | "status" | "timestamp">) {
  const reference = await addDoc(collection(db, "offers"), {
    ...input,
    status: "PENDING",
    timestamp: serverTimestamp(),
    updatedAt: serverTimestamp(),
  });
  return reference.id;
}

export function listenOffers(uid: string, callback: (items: Offer[]) => void): Unsubscribe {
  const buyerQuery = query(collection(db, "offers"), where("buyerId", "==", uid), limit(100));
  const sellerQuery = query(collection(db, "offers"), where("sellerId", "==", uid), limit(100));
  let buyerOffers: Offer[] = [];
  let sellerOffers: Offer[] = [];
  const publish = () => {
    const unique = new Map([...buyerOffers, ...sellerOffers].map((item) => [item.id, item]));
    callback(Array.from(unique.values()));
  };
  const unsubBuyer = onSnapshot(buyerQuery, (snapshot) => { buyerOffers = snapshot.docs.map((item) => mapOffer(item.id, item.data())); publish(); });
  const unsubSeller = onSnapshot(sellerQuery, (snapshot) => { sellerOffers = snapshot.docs.map((item) => mapOffer(item.id, item.data())); publish(); });
  return () => { unsubBuyer(); unsubSeller(); };
}

export async function updateOffer(id: string, status: Offer["status"], counterPriceDzd?: number) {
  await updateDoc(doc(db, "offers", id), {
    status,
    ...(counterPriceDzd ? { counterPriceDzd } : {}),
    updatedAt: serverTimestamp(),
  });
}

export async function loadUser(uid: string): Promise<MarketplaceUser | null> {
  const snapshot = await getDoc(doc(db, "users", uid));
  if (!snapshot.exists()) return null;
  const data = snapshot.data();
  return { id: uid, name: data.name ?? "", email: data.email, phone: data.phone, wilayaCode: data.wilayaCode, isBlocked: Boolean(data.isBlocked) };
}

export function listenUsers(callback: (items: AdminUser[]) => void, onError: (error: Error) => void): Unsubscribe {
  return onSnapshot(query(collection(db, "users"), orderBy("name"), limit(200)), (snapshot) => {
    callback(snapshot.docs.map((item) => ({ id: item.id, name: item.data().name ?? "مستخدم بدون اسم", phone: item.data().phone ?? "", email: item.data().email, wilayaCode: item.data().wilayaCode, isBlocked: Boolean(item.data().isBlocked) })));
  }, onError);
}

export async function setUserBlocked(uid: string, blocked: boolean) {
  await updateDoc(doc(db, "users", uid), { isBlocked: blocked, updatedAt: serverTimestamp() });
}

export async function setListingStatus(id: string, status: Listing["status"], rejectionReason = "") {
  await updateDoc(doc(db, "listings", id), { status, ...(rejectionReason ? { rejectionReason } : {}), updatedAt: serverTimestamp() });
}
