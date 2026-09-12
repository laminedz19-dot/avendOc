package com.example.data

import java.util.UUID

enum class CategoryType(
    val id: String,
    val titleAr: String,
    val titleFr: String,
    val iconName: String
) {
    VEHICLES("vehicles", "السيارات والمركبات", "Véhicules & Auto", "DirectionsCar"),
    REAL_ESTATE("real_estate", "العقارات والمنازل", "Immobilier", "Apartment"),
    PHONES_TECH("phones_tech", "الهواتف والإلكترونيات", "Téléphones & Informatique", "Smartphone"),
    HOME_APPLIANCES("appliances", "الأجهزة الكهرومنزلية", "Électroménager", "Kitchen"),
    FASHION("fashion", "الملابس والموضة", "Mode & Vêtements", "Checkroom"),
    FURNITURE("furniture", "الأثاث والديكور", "Meubles & Déco", "Weekend"),
    TOOLS_INDUSTRY("tools", "المعدات والمهن", "Matériel & Outillage", "Build"),
    SERVICES("services", "الخدمات والوظائف", "Services & Emploi", "Work"),
    OTHER("other", "أخرى ومتنوعة", "Autres", "Category")
}

enum class ItemCondition(val labelAr: String, val labelFr: String) {
    NEW("جديد مغلف (Neuf)", "Neuf sous emballage"),
    LIKE_NEW("شبه جديد (Comme neuf)", "Comme neuf"),
    GOOD("حالة جيدة (Bon état)", "Bon état"),
    USED("مستعمل عادي (État correct)", "État correct"),
    FOR_PARTS("لقطع الغيار (Pour pièces)", "Pour pièces")
}

enum class DeliveryOption(val labelAr: String, val labelFr: String, val subtitleAr: String, val subtitleFr: String) {
    ALL_69_WILAYAS(
        "توصيل متوفر لـ 69 ولاية",
        "Livraison disponible 69 wilayas",
        "عبر Yalidine Express / Maystro حتى باب المنزل أو المكتب",
        "Via Yalidine Express / Maystro à domicile ou en point relais"
    ),
    HAND_TO_HAND(
        "استلام يد بيد في مكان المعاينة",
        "Remise en main propre",
        "المقابلة المباشرة مع المشتري والفحص قبل الدفع",
        "Rencontre directe et vérification avant paiement"
    ),
    LOCAL_WILAYA(
        "توصيل محلي في حدود الولاية فقط",
        "Livraison locale (Wilaya uniquement)",
        "التوصيل السريع داخل بلديات الولاية فقط",
        "Livraison rapide dans les communes de la wilaya seulement"
    )
}

enum class AdStatus(val labelAr: String, val labelFr: String) {
    PAYMENT_REQUIRED("يتطلب دفع 300 دج", "Paiement 300 DZD requis"),
    PAYMENT_PENDING("قيد مراجعة إيصال الدفع", "Vérification CCP/BaridiMob en cours"),
    PUBLISHED("منشور في السوق", "Publié & Actif"),
    REJECTED("مرفوض (إيصال غير صالح)", "Rejeté (Reçu invalide)"),
    SOLD("تم البيع", "Vendu")
}

enum class OfferStatus(val labelAr: String, val labelFr: String) {
    PENDING("قيد الانتظار", "En attente"),
    ACCEPTED("تم قبول العرض", "Offre acceptée"),
    REJECTED("تم رفض العرض", "Offre refusée"),
    COUNTER_OFFER("عرض مضاد من البائع", "Contre-offre du vendeur")
}

data class ListingItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val priceDzd: Long,
    val isNegotiable: Boolean = true,
    val category: CategoryType,
    val wilayaCode: String,
    val wilayaNameAr: String,
    val wilayaNameFr: String,
    val commune: String,
    val condition: ItemCondition,
    val sellerId: String,
    val sellerName: String,
    val sellerPhone: String,
    val isSellerVerified: Boolean = false,
    val status: AdStatus = AdStatus.PAYMENT_REQUIRED,
    val paymentReference: String = "",
    val paymentDate: String = "",
    val paymentProofReceiptUrl: String = "",
    val images: List<String> = emptyList(),
    val deliveryOption: DeliveryOption = DeliveryOption.ALL_69_WILAYAS,
    val createdAt: String = "",
    val viewsCount: Int = 0,
    val offersCount: Int = 0,
    val featuredTag: String? = null
)

data class NegotiationOffer(
    val id: String = UUID.randomUUID().toString(),
    val listingId: String,
    val sellerId: String = "",
    val listingTitle: String,
    val buyerId: String,
    val buyerName: String,
    val buyerPhone: String,
    val originalPriceDzd: Long,
    val proposedPriceDzd: Long,
    val counterPriceDzd: Long? = null,
    val status: OfferStatus = OfferStatus.PENDING,
    val message: String = "",
    val timestamp: String = ""
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val listingId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: String = "",
    val isFromMe: Boolean = true
)

data class PaymentVerificationRecord(
    val id: String = UUID.randomUUID().toString(),
    val listingId: String,
    val listingTitle: String,
    val sellerName: String,
    val sellerPhone: String,
    val wilaya: String,
    val amountDzd: Int = 300,
    val transactionRef: String,
    val paymentMethod: String = "BaridiMob / CCP",
    val receiptImage: String = "",
    val submittedAt: String = "",
    val status: AdStatus = AdStatus.PAYMENT_PENDING,
    val rejectReason: String? = null
)

data class PlatformSettings(
    val ccpAccount: String = "0008761821",
    val ccpKey: String = "94",
    val ccpName: String = "AchriDZ Algérie Marketplace",
    val baridiMobRip: String = "007999990008761821",
    val feeAmountDzd: Int = 300,
    val supportPhone: String = "0550000000",
    val supportEmail: String = "contact@achridz.dz"
)

data class ReceiptVerificationResult(
    val isValid: Boolean = false,
    val isScanning: Boolean = false,
    val extractedAccount: String? = null,
    val extractedKey: String? = null,
    val extractedAmountDzd: Int? = null,
    val extractedTransactionRef: String? = null,
    val extractedDate: String? = null,
    val validationMessage: String = "",
    val receiptImageUri: String? = null
)

data class UserAccount(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val wilayaCode: String = "16",
    val isLoggedIn: Boolean = false,
    val isBlocked: Boolean = false
)

enum class UserRole(val displayNameAr: String, val displayNameFr: String) {
    BUYER("كريم (مشتري)", "Karim (Acheteur)"),
    SELLER("أمين (بائع)", "Amine (Vendeur)"),
}

enum class AppLanguage {
    ARABIC,
    FRENCH
}
