package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MarketplaceRepository {

    private val _listings = MutableStateFlow<List<ListingItem>>(SampleData.initialListings)
    val listings: StateFlow<List<ListingItem>> = _listings.asStateFlow()

    private val _offers = MutableStateFlow<List<NegotiationOffer>>(SampleData.initialOffers)
    val offers: StateFlow<List<NegotiationOffer>> = _offers.asStateFlow()

    private val _chats = MutableStateFlow<List<ChatMessage>>(SampleData.initialChats)
    val chats: StateFlow<List<ChatMessage>> = _chats.asStateFlow()

    private val _verifications = MutableStateFlow<List<PaymentVerificationRecord>>(SampleData.initialVerifications)
    val verifications: StateFlow<List<PaymentVerificationRecord>> = _verifications.asStateFlow()

    private val _platformSettings = MutableStateFlow(PlatformSettings())
    val platformSettings: StateFlow<PlatformSettings> = _platformSettings.asStateFlow()

    private val _currentUserRole = MutableStateFlow(UserRole.SELLER)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ARABIC)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(setOf("ad-1", "ad-2"))
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

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
        sellerPhone: String,
        paymentRef: String,
        paymentDate: String,
        isDemoAccount: Boolean = false
    ): ListingItem {
        val wilaya = WilayasData.findWilayaByCode(wilayaCode)
        val wilayaNameAr = wilaya?.nameAr ?: "الجزائر"
        val wilayaNameFr = wilaya?.nameFr ?: "Alger"

        val initialStatus = if (paymentRef.isNotBlank()) AdStatus.PAYMENT_PENDING else AdStatus.PAYMENT_REQUIRED

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
            sellerId = if (_currentUserRole.value == UserRole.SELLER) "seller-amine" else "buyer-karim",
            sellerName = if (_currentUserRole.value == UserRole.SELLER) "أمين قاسي" else "كريم منصوري",
            sellerPhone = sellerPhone.ifBlank { "0661234567" },
            status = initialStatus,
            paymentReference = paymentRef,
            paymentDate = paymentDate,
            isDemoAccount = isDemoAccount,
            createdAt = "الآن"
        )

        _listings.update { listOf(newAd) + it }

        if (paymentRef.isNotBlank()) {
            val verification = PaymentVerificationRecord(
                listingId = newAd.id,
                listingTitle = newAd.title,
                sellerName = newAd.sellerName,
                sellerPhone = newAd.sellerPhone,
                wilaya = "$wilayaCode - $wilayaNameAr",
                amountDzd = 200,
                transactionRef = paymentRef,
                submittedAt = "الآن",
                status = AdStatus.PAYMENT_PENDING
            )
            _verifications.update { listOf(verification) + it }
        }

        return newAd
    }

    fun submitPaymentProof(listingId: String, referenceNumber: String, paymentDate: String) {
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
                amountDzd = 200,
                transactionRef = referenceNumber,
                submittedAt = "الآن",
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
        val offer = NegotiationOffer(
            listingId = listingId,
            listingTitle = listingTitle,
            buyerId = "buyer-karim",
            buyerName = "كريم منصوري",
            buyerPhone = "0559876543",
            originalPriceDzd = originalPriceDzd,
            proposedPriceDzd = proposedPriceDzd,
            status = OfferStatus.PENDING,
            message = message,
            timestamp = "الآن"
        )
        _offers.update { listOf(offer) + it }
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
                    if (counterPrice != null && counterPrice > 0) {
                        offer.copy(status = OfferStatus.COUNTER_OFFER, counterPriceDzd = counterPrice)
                    } else if (accept) {
                        offer.copy(status = OfferStatus.ACCEPTED)
                    } else {
                        offer.copy(status = OfferStatus.REJECTED)
                    }
                } else offer
            }
        }
    }

    fun sendChatMessage(listingId: String, text: String) {
        val isSeller = _currentUserRole.value == UserRole.SELLER
        val msg = ChatMessage(
            listingId = listingId,
            senderId = if (isSeller) "seller-amine" else "buyer-karim",
            senderName = if (isSeller) "أمين قاسي" else "كريم منصوري",
            text = text,
            timestamp = "الآن",
            isFromMe = true
        )
        _chats.update { it + msg }
    }

    fun markAsSold(listingId: String) {
        _listings.update { list ->
            list.map {
                if (it.id == listingId) it.copy(status = AdStatus.SOLD) else it
            }
        }
    }
}
