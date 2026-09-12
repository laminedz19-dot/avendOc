package com.example.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MarketplaceRepository {

    private val cloudService = FirebaseFirestoreService()
    private val auth = FirebaseAuth.getInstance()

    private val _listings = MutableStateFlow<List<ListingItem>>(emptyList())
    val listings: StateFlow<List<ListingItem>> = _listings.asStateFlow()

    private val _offers = MutableStateFlow<List<NegotiationOffer>>(emptyList())
    val offers: StateFlow<List<NegotiationOffer>> = _offers.asStateFlow()

    private val _chats = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chats: StateFlow<List<ChatMessage>> = _chats.asStateFlow()

    private val _verifications = MutableStateFlow<List<PaymentVerificationRecord>>(emptyList())
    val verifications: StateFlow<List<PaymentVerificationRecord>> = _verifications.asStateFlow()

    private val _platformSettings = MutableStateFlow(PlatformSettings())
    val platformSettings: StateFlow<PlatformSettings> = _platformSettings.asStateFlow()

    private val _currentUserRole = MutableStateFlow(UserRole.SELLER)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ARABIC)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    init {
        // Initialize Firebase Firestore cloud sync listener
        cloudService.initialize { remoteListings ->
            _listings.value = remoteListings
        }
    }

    fun syncUser(account: UserAccount) {
        cloudService.syncUser(account)
    }

    fun observeUserBlock(userId: String, onBlockedChanged: (Boolean) -> Unit) {
        cloudService.listenToUserBlock(userId, onBlockedChanged)
    }

    fun switchRole(role: UserRole) {
        _currentUserRole.value = role
    }

    fun toggleLanguage() {
        _currentLanguage.update {
            if (it == AppLanguage.ARABIC) AppLanguage.FRENCH else AppLanguage.ARABIC
        }
    }

    fun toggleFavorite(listingId: String) {
        _favoriteIds.update { set ->
            if (set.contains(listingId)) set - listingId else set + listingId
        }
    }

    fun incrementView(listingId: String) {
        _listings.update { list ->
            list.map {
                if (it.id == listingId) it.copy(viewsCount = it.viewsCount + 1) else it
            }
        }
    }

    fun createListing(
        title: String,
        description: String,
        priceDzd: Long,
        isNegotiable: Boolean,
        category: CategoryType,
        wilayaCode: String,
        commune: String,
        condition: ItemCondition,
        sellerId: String,
        sellerName: String,
        sellerPhone: String,
        images: List<String> = emptyList(),
        deliveryOption: DeliveryOption = DeliveryOption.ALL_69_WILAYAS,
        paymentRef: String = "",
        paymentDate: String = "",
    ): ListingItem {
        val currentUser = auth.currentUser ?: error("يجب تسجيل الدخول قبل إنشاء إعلان")
        val wilaya = WilayasData.findWilayaByCode(wilayaCode)
        val wilayaNameAr = wilaya?.nameAr ?: "الجزائر"
        val wilayaNameFr = wilaya?.nameFr ?: "Alger"

        // Every new ad is hidden from the marketplace until an admin approves it.
        val initialStatus = AdStatus.PAYMENT_PENDING

        val newAd = ListingItem(
            title = title,
            description = description,
            priceDzd = priceDzd,
            isNegotiable = isNegotiable,
            category = category,
            wilayaCode = wilayaCode,
            wilayaNameAr = wilayaNameAr,
            wilayaNameFr = wilayaNameFr,
            commune = commune.ifBlank { wilaya?.communes?.firstOrNull() ?: wilayaNameAr },
            condition = condition,
            images = images,
            deliveryOption = deliveryOption,
            sellerId = currentUser.uid,
            sellerName = sellerName,
            sellerPhone = sellerPhone,
            status = initialStatus,
            paymentReference = paymentRef,
            paymentDate = paymentDate,
            createdAt = ""
        )

        _listings.update { listOf(newAd) + it }
        cloudService.syncListing(newAd)

        return newAd
    }

    fun submitPaymentProof(listingId: String, referenceNumber: String, paymentDate: String) {
        if (auth.currentUser == null) return
        _listings.update { list ->
            list.map { ad ->
                if (ad.id == listingId) {
                    ad.copy(
                        status = AdStatus.PAYMENT_PENDING,
                        paymentReference = referenceNumber,
                        paymentDate = paymentDate
                    )
                } else ad
            }
        }

        val ad = _listings.value.find { it.id == listingId }
        if (ad != null) {
            val verification = PaymentVerificationRecord(
                listingId = ad.id,
                listingTitle = ad.title,
                sellerName = ad.sellerName,
                sellerPhone = ad.sellerPhone,
                wilaya = "${ad.wilayaCode} - ${ad.wilayaNameAr}",
                amountDzd = 300,
                transactionRef = referenceNumber,
                submittedAt = "",
                status = AdStatus.PAYMENT_PENDING
            )
            _verifications.update { listOf(verification) + it.filterNot { it.listingId == listingId } }
        }
    }

    fun approvePayment(verificationId: String) {
        val verification = _verifications.value.find { it.id == verificationId } ?: return
        val targetListingId = verification.listingId

        _verifications.update { list ->
            list.map {
                if (it.id == verificationId) it.copy(status = AdStatus.PUBLISHED) else it
            }
        }

        _listings.update { list ->
            list.map {
                if (it.id == targetListingId) it.copy(status = AdStatus.PUBLISHED) else it
            }
        }
    }

    fun rejectPayment(verificationId: String, reason: String) {
        val verification = _verifications.value.find { it.id == verificationId } ?: return
        val targetListingId = verification.listingId

        _verifications.update { list ->
            list.map {
                if (it.id == verificationId) it.copy(status = AdStatus.REJECTED, rejectReason = reason) else it
            }
        }

        _listings.update { list ->
            list.map {
                if (it.id == targetListingId) it.copy(status = AdStatus.REJECTED) else it
            }
        }
    }

    fun makeOffer(
        listingId: String,
        listingTitle: String,
        originalPriceDzd: Long,
        proposedPriceDzd: Long,
        message: String
    ) {
        val currentUser = auth.currentUser ?: return
        val listing = _listings.value.firstOrNull { it.id == listingId } ?: return
        if (listing.sellerId == currentUser.uid) return
        val offer = NegotiationOffer(
            listingId = listingId,
            sellerId = listing.sellerId,
            listingTitle = listingTitle,
            buyerId = currentUser.uid,
            buyerName = currentUser.displayName.orEmpty(),
            buyerPhone = currentUser.phoneNumber.orEmpty(),
            originalPriceDzd = originalPriceDzd,
            proposedPriceDzd = proposedPriceDzd,
            status = OfferStatus.PENDING,
            message = message,
            timestamp = ""
        )
        _offers.update { listOf(offer) + it }
        cloudService.syncOffer(offer)
        _listings.update { list ->
            list.map {
                if (it.id == listingId) it.copy(offersCount = it.offersCount + 1) else it
            }
        }
    }

    fun respondToOffer(offerId: String, accept: Boolean, counterPrice: Long? = null) {
        _offers.update { list ->
            list.map { offer ->
                if (offer.id == offerId) {
                    val updated = if (counterPrice != null && counterPrice > 0) {
                        offer.copy(status = OfferStatus.COUNTER_OFFER, counterPriceDzd = counterPrice)
                    } else if (accept) {
                        offer.copy(status = OfferStatus.ACCEPTED)
                    } else {
                        offer.copy(status = OfferStatus.REJECTED)
                    }
                    cloudService.syncOffer(updated)
                    updated
                } else offer
            }
        }
    }

    fun sendChatMessage(listingId: String, text: String) {
        val currentUser = auth.currentUser ?: return
        if (text.isBlank()) return
        val msg = ChatMessage(
            listingId = listingId,
            senderId = currentUser.uid,
            senderName = currentUser.displayName.orEmpty(),
            text = text,
            timestamp = "",
            isFromMe = true
        )
        _chats.update { it + msg }
        cloudService.syncChatMessage(msg)
    }

    fun markAsSold(listingId: String) {
        if (auth.currentUser == null) return
        _listings.update { list ->
            list.map {
                if (it.id == listingId) it.copy(status = AdStatus.SOLD) else it
            }
        }
    }
}
