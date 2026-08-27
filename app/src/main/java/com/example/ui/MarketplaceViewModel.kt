package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AdStatus
import com.example.data.AppLanguage
import com.example.data.CategoryType
import com.example.data.ChatMessage
import com.example.data.ItemCondition
import com.example.data.ListingItem
import com.example.data.MarketplaceRepository
import com.example.data.NegotiationOffer
import com.example.data.OfferStatus
import com.example.data.PaymentVerificationRecord
import com.example.data.PlatformSettings
import com.example.data.UserRole
import com.example.data.Wilaya
import com.example.data.WilayasData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class NavigationTab {
    MARKETPLACE,
    POST_AD,
    OFFERS_CHAT,
    MY_ADS,
    ADMIN
}

data class FilterState(
    val searchQuery: String = "",
    val selectedCategory: CategoryType? = null,
    val selectedWilayaCode: String? = null,
    val selectedCondition: ItemCondition? = null,
    val onlyNegotiable: Boolean = false,
    val maxPriceDzd: Long? = null
)

data class CreateAdFormState(
    val currentStep: Int = 1,
    val totalSteps: Int = 10,
    val title: String = "",
    val description: String = "",
    val priceText: String = "",
    val isNegotiable: Boolean = true,
    val category: CategoryType = CategoryType.VEHICLES,
    val wilayaCode: String = "16",
    val commune: String = "",
    val condition: ItemCondition = ItemCondition.LIKE_NEW,
    val sellerPhone: String = "0661234567",
    val paymentReference: String = "",
    val paymentDate: String = "",
    val isDemoAccount: Boolean = false,
    val isSubmitting: Boolean = false,
    val submittedAdId: String? = null
)

class MarketplaceViewModel(
    private val repository: MarketplaceRepository = MarketplaceRepository()
) : ViewModel() {

    val currentUserRole: StateFlow<UserRole> = repository.currentUserRole
    val currentLanguage: StateFlow<AppLanguage> = repository.currentLanguage
    val favoriteIds: StateFlow<Set<String>> = repository.favoriteIds
    val platformSettings: StateFlow<PlatformSettings> = repository.platformSettings
    val offers: StateFlow<List<NegotiationOffer>> = repository.offers
    val chats: StateFlow<List<ChatMessage>> = repository.chats
    val verifications: StateFlow<List<PaymentVerificationRecord>> = repository.verifications

    private val _selectedTab = MutableStateFlow(NavigationTab.MARKETPLACE)
    val selectedTab: StateFlow<NavigationTab> = _selectedTab.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _selectedListing = MutableStateFlow<ListingItem?>(null)
    val selectedListing: StateFlow<ListingItem?> = _selectedListing.asStateFlow()

    private val _createAdForm = MutableStateFlow(CreateAdFormState())
    val createAdForm: StateFlow<CreateAdFormState> = _createAdForm.asStateFlow()

    private val _activeChatListingId = MutableStateFlow<String?>("ad-1")
    val activeChatListingId: StateFlow<String?> = _activeChatListingId.asStateFlow()

    val filteredListings: StateFlow<List<ListingItem>> = combine(
        repository.listings,
        _filterState
    ) { listings, filters ->
        listings.filter { item ->
            val matchesSearch = filters.searchQuery.isBlank() ||
                    item.title.contains(filters.searchQuery, ignoreCase = true) ||
                    item.description.contains(filters.searchQuery, ignoreCase = true) ||
                    item.wilayaNameAr.contains(filters.searchQuery, ignoreCase = true) ||
                    item.wilayaNameFr.contains(filters.searchQuery, ignoreCase = true)

            val matchesCategory = filters.selectedCategory == null || item.category == filters.selectedCategory
            val matchesWilaya = filters.selectedWilayaCode == null || item.wilayaCode == filters.selectedWilayaCode
            val matchesCondition = filters.selectedCondition == null || item.condition == filters.selectedCondition
            val matchesNegotiable = !filters.onlyNegotiable || item.isNegotiable
            val matchesPrice = filters.maxPriceDzd == null || item.priceDzd <= filters.maxPriceDzd

            // In general marketplace, show PUBLISHED items
            matchesSearch && matchesCategory && matchesWilaya && matchesCondition && matchesNegotiable && matchesPrice
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myAds: StateFlow<List<ListingItem>> = combine(
        repository.listings,
        currentUserRole
    ) { listings, role ->
        val currentSellerId = if (role == UserRole.SELLER) "seller-amine" else "buyer-karim"
        listings.filter { it.sellerId == currentSellerId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: NavigationTab) {
        _selectedTab.value = tab
    }

    fun switchRole(role: UserRole) {
        repository.switchRole(role)
    }

    fun toggleLanguage() {
        repository.toggleLanguage()
    }

    fun toggleFavorite(listingId: String) {
        repository.toggleFavorite(listingId)
    }

    fun selectListing(listing: ListingItem?) {
        _selectedListing.value = listing
        if (listing != null) {
            repository.incrementView(listing.id)
            _activeChatListingId.value = listing.id
        }
    }

    fun updateSearchQuery(query: String) {
        _filterState.update { it.copy(searchQuery = query) }
    }

    fun selectCategoryFilter(category: CategoryType?) {
        _filterState.update {
            it.copy(selectedCategory = if (it.selectedCategory == category) null else category)
        }
    }

    fun selectWilayaFilter(wilayaCode: String?) {
        _filterState.update {
            it.copy(selectedWilayaCode = if (it.selectedWilayaCode == wilayaCode) null else wilayaCode)
        }
    }

    fun toggleOnlyNegotiable() {
        _filterState.update { it.copy(onlyNegotiable = !it.onlyNegotiable) }
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    // Ad Creation Form handlers
    fun updateCreateForm(transform: (CreateAdFormState) -> CreateAdFormState) {
        _createAdForm.update(transform)
    }

    fun nextFormStep() {
        _createAdForm.update {
            if (it.currentStep < it.totalSteps) it.copy(currentStep = it.currentStep + 1) else it
        }
    }

    fun prevFormStep() {
        _createAdForm.update {
            if (it.currentStep > 1) it.copy(currentStep = it.currentStep - 1) else it
        }
    }

    fun resetCreateForm() {
        _createAdForm.value = CreateAdFormState()
    }

    fun submitCreateAd() {
        val form = _createAdForm.value
        val price = form.priceText.toLongOrNull() ?: 10000L

        val created = repository.createListing(
            title = form.title.ifBlank { "منتج مستعمل للبيع" },
            description = form.description.ifBlank { "تفاصيل المنتج وحالته العامة" },
            priceDzd = price,
            isNegotiable = form.isNegotiable,
            category = form.category,
            wilayaCode = form.wilayaCode,
            commune = form.commune,
            condition = form.condition,
            sellerPhone = form.sellerPhone,
            paymentRef = form.paymentReference,
            paymentDate = form.paymentDate.ifBlank { "2026-08-27" },
            isDemoAccount = form.isDemoAccount
        )

        _createAdForm.update {
            it.copy(submittedAdId = created.id, currentStep = 10)
        }
    }

    // 200 DZD Payment Verification Handlers
    fun submitPaymentForAd(listingId: String, reference: String, date: String) {
        repository.submitPaymentProof(listingId, reference, date)
    }

    fun approvePayment(verificationId: String) {
        repository.approvePayment(verificationId)
    }

    fun rejectPayment(verificationId: String, reason: String) {
        repository.rejectPayment(verificationId, reason)
    }

    // Negotiation
    fun sendOffer(listing: ListingItem, proposedPrice: Long, message: String) {
        repository.makeOffer(
            listingId = listing.id,
            listingTitle = listing.title,
            originalPriceDzd = listing.priceDzd,
            proposedPriceDzd = proposedPrice,
            message = message
        )
    }

    fun respondToOffer(offerId: String, accept: Boolean, counterPrice: Long? = null) {
        repository.respondToOffer(offerId, accept, counterPrice)
    }

    // Direct Chat
    fun setActiveChat(listingId: String) {
        _activeChatListingId.value = listingId
    }

    fun sendMessage(text: String) {
        val listingId = _activeChatListingId.value ?: return
        if (text.isNotBlank()) {
            repository.sendChatMessage(listingId, text.trim())
        }
    }

    fun markAdSold(listingId: String) {
        repository.markAsSold(listingId)
    }
}
