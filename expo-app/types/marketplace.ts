export const CATEGORIES = [
  { id: "vehicles", ar: "السيارات والمركبات", fr: "Véhicules & Auto" },
  { id: "real_estate", ar: "العقارات والمنازل", fr: "Immobilier" },
  { id: "phones_tech", ar: "الهواتف والإلكترونيات", fr: "Téléphones & Informatique" },
  { id: "appliances", ar: "الأجهزة الكهرومنزلية", fr: "Électroménager" },
  { id: "fashion", ar: "الملابس والموضة", fr: "Mode & Vêtements" },
  { id: "furniture", ar: "الأثاث والديكور", fr: "Meubles & Déco" },
  { id: "tools", ar: "المعدات والمهن", fr: "Matériel & Outillage" },
  { id: "services", ar: "الخدمات والوظائف", fr: "Services & Emploi" },
  { id: "other", ar: "أخرى ومتنوعة", fr: "Autres" },
] as const;

export const CONDITIONS = [
  { id: "NEW", ar: "جديد مغلف", fr: "Neuf sous emballage" },
  { id: "LIKE_NEW", ar: "شبه جديد", fr: "Comme neuf" },
  { id: "GOOD", ar: "حالة جيدة", fr: "Bon état" },
  { id: "USED", ar: "مستعمل عادي", fr: "État correct" },
  { id: "FOR_PARTS", ar: "لقطع الغيار", fr: "Pour pièces" },
] as const;

export const DELIVERY_OPTIONS = [
  { id: "ALL_69_WILAYAS", ar: "توصيل إلى 69 ولاية", fr: "Livraison 69 wilayas" },
  { id: "HAND_TO_HAND", ar: "استلام يد بيد", fr: "Remise en main propre" },
  { id: "LOCAL_WILAYA", ar: "توصيل محلي داخل الولاية", fr: "Livraison locale" },
] as const;

export type ListingStatus = "PAYMENT_REQUIRED" | "PAYMENT_PENDING" | "PUBLISHED" | "REJECTED" | "SOLD";
export type OfferStatus = "PENDING" | "ACCEPTED" | "REJECTED" | "COUNTER_OFFER";

export type Listing = {
  id: string;
  title: string;
  description: string;
  priceDzd: number;
  isNegotiable: boolean;
  category: string;
  wilayaCode: string;
  wilayaNameAr: string;
  wilayaNameFr: string;
  commune: string;
  condition: string;
  sellerId: string;
  sellerName: string;
  sellerPhone: string;
  status: ListingStatus;
  paymentReference?: string;
  paymentDate?: string;
  paymentProofReceiptUrl?: string;
  images?: string[];
  deliveryOption?: string;
  createdAt?: string;
  viewsCount?: number;
  offersCount?: number;
  rejectionReason?: string;
};

export type Offer = {
  id: string;
  listingId: string;
  listingTitle: string;
  sellerId: string;
  buyerId: string;
  buyerName: string;
  buyerPhone: string;
  originalPriceDzd: number;
  proposedPriceDzd: number;
  counterPriceDzd?: number | null;
  status: OfferStatus;
  message?: string;
  timestamp?: string;
};

export type MarketplaceUser = {
  id: string;
  name: string;
  email?: string;
  phone?: string;
  wilayaCode?: string;
  isBlocked?: boolean;
};

export type AdminUser = MarketplaceUser & { isBlocked: boolean };
