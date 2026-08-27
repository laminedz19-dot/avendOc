package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdStatus
import com.example.data.AppLanguage
import com.example.data.OfferStatus
import com.example.ui.components.AchriDZTopBar
import com.example.ui.screens.AdDetailScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CreateAdScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyAdsScreen
import com.example.ui.screens.OffersAndChatScreen
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SlateMuted

@Composable
fun MainAppContainer(
    viewModel: MarketplaceViewModel
) {
    val currentRole by viewModel.currentUserRole.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val filteredListings by viewModel.filteredListings.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val selectedListing by viewModel.selectedListing.collectAsState()
    val createAdForm by viewModel.createAdForm.collectAsState()
    val platformSettings by viewModel.platformSettings.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val chats by viewModel.chats.collectAsState()
    val verifications by viewModel.verifications.collectAsState()
    val myAds by viewModel.myAds.collectAsState()

    val isArabic = currentLanguage == AppLanguage.ARABIC
    val pendingVerifCount = verifications.count { it.status == AdStatus.PAYMENT_PENDING }
    val pendingOffersCount = offers.count { it.status == OfferStatus.PENDING }

    if (selectedListing != null) {
        AdDetailScreen(
            listing = selectedListing!!,
            isFavorite = favoriteIds.contains(selectedListing!!.id),
            currentLanguage = currentLanguage,
            chats = chats,
            onToggleFavorite = { viewModel.toggleFavorite(selectedListing!!.id) },
            onBack = { viewModel.selectListing(null) },
            onSendOffer = { price, msg ->
                viewModel.sendOffer(selectedListing!!, price, msg)
            },
            onSendMessage = { text ->
                viewModel.sendMessage(text)
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AchriDZTopBar(
                    currentRole = currentRole,
                    currentLanguage = currentLanguage,
                    onRoleSelected = { role -> viewModel.switchRole(role) },
                    onToggleLanguage = { viewModel.toggleLanguage() }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_nav")
                ) {
                    // Tab 1: Marketplace
                    NavigationBarItem(
                        selected = selectedTab == NavigationTab.MARKETPLACE,
                        onClick = { viewModel.selectTab(NavigationTab.MARKETPLACE) },
                        icon = {
                            Icon(imageVector = Icons.Default.Storefront, contentDescription = "Marketplace")
                        },
                        label = {
                            Text(
                                text = if (isArabic) "السوق" else "Marché",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == NavigationTab.MARKETPLACE) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_marketplace")
                    )

                    // Tab 2: Post Ad
                    NavigationBarItem(
                        selected = selectedTab == NavigationTab.POST_AD,
                        onClick = { viewModel.selectTab(NavigationTab.POST_AD) },
                        icon = {
                            Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Post Ad")
                        },
                        label = {
                            Text(
                                text = if (isArabic) "نشر إعلان" else "Publier",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == NavigationTab.POST_AD) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_post_ad")
                    )

                    // Tab 3: Bargaining & Chat
                    NavigationBarItem(
                        selected = selectedTab == NavigationTab.OFFERS_CHAT,
                        onClick = { viewModel.selectTab(NavigationTab.OFFERS_CHAT) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (pendingOffersCount > 0) {
                                        Badge(containerColor = AmberAccent) {
                                            Text("$pendingOffersCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Handshake, contentDescription = "Offers & Chat")
                            }
                        },
                        label = {
                            Text(
                                text = if (isArabic) "المساومة" else "Négociations",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == NavigationTab.OFFERS_CHAT) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_offers_chat")
                    )

                    // Tab 4: My Ads
                    NavigationBarItem(
                        selected = selectedTab == NavigationTab.MY_ADS,
                        onClick = { viewModel.selectTab(NavigationTab.MY_ADS) },
                        icon = {
                            Icon(imageVector = Icons.Default.Inventory, contentDescription = "My Ads")
                        },
                        label = {
                            Text(
                                text = if (isArabic) "إعلاناتي" else "Mes annonces",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == NavigationTab.MY_ADS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_my_ads")
                    )

                    // Tab 5: Admin CCP Verification
                    NavigationBarItem(
                        selected = selectedTab == NavigationTab.ADMIN,
                        onClick = { viewModel.selectTab(NavigationTab.ADMIN) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (pendingVerifCount > 0) {
                                        Badge(containerColor = AmberAccent) {
                                            Text("$pendingVerifCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = "Admin")
                            }
                        },
                        label = {
                            Text(
                                text = if (isArabic) "مراقبة CCP" else "Admin CCP",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == NavigationTab.ADMIN) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_admin")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    NavigationTab.MARKETPLACE -> {
                        HomeScreen(
                            listings = filteredListings,
                            filterState = filterState,
                            favoriteIds = favoriteIds,
                            currentLanguage = currentLanguage,
                            onSearchChange = { viewModel.updateSearchQuery(it) },
                            onCategorySelect = { viewModel.selectCategoryFilter(it) },
                            onWilayaSelect = { viewModel.selectWilayaFilter(it) },
                            onToggleNegotiable = { viewModel.toggleOnlyNegotiable() },
                            onResetFilters = { viewModel.resetFilters() },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onListingClick = { viewModel.selectListing(it) },
                            onPostAdClick = { viewModel.selectTab(NavigationTab.POST_AD) }
                        )
                    }

                    NavigationTab.POST_AD -> {
                        CreateAdScreen(
                            formState = createAdForm,
                            platformSettings = platformSettings,
                            currentLanguage = currentLanguage,
                            onFormChange = { viewModel.updateCreateForm(it) },
                            onNextStep = { viewModel.nextFormStep() },
                            onPrevStep = { viewModel.prevFormStep() },
                            onSubmit = { viewModel.submitCreateAd() },
                            onReset = { viewModel.resetCreateForm() },
                            onViewMyAds = {
                                viewModel.resetCreateForm()
                                viewModel.selectTab(NavigationTab.MY_ADS)
                            }
                        )
                    }

                    NavigationTab.OFFERS_CHAT -> {
                        OffersAndChatScreen(
                            offers = offers,
                            chats = chats,
                            currentLanguage = currentLanguage,
                            onRespondOffer = { id, accept, counter ->
                                viewModel.respondToOffer(id, accept, counter)
                            },
                            onSendMessage = { text ->
                                viewModel.sendMessage(text)
                            }
                        )
                    }

                    NavigationTab.MY_ADS -> {
                        MyAdsScreen(
                            myAds = myAds,
                            platformSettings = platformSettings,
                            currentLanguage = currentLanguage,
                            onPostAdClick = { viewModel.selectTab(NavigationTab.POST_AD) },
                            onSubmitPaymentForAd = { id, ref, date ->
                                viewModel.submitPaymentForAd(id, ref, date)
                            },
                            onMarkAsSold = { viewModel.markAdSold(it) },
                            onAdClick = { viewModel.selectListing(it) }
                        )
                    }

                    NavigationTab.ADMIN -> {
                        AdminScreen(
                            verifications = verifications,
                            platformSettings = platformSettings,
                            currentLanguage = currentLanguage,
                            onApprovePayment = { viewModel.approvePayment(it) },
                            onRejectPayment = { id, reason -> viewModel.rejectPayment(id, reason) }
                        )
                    }
                }
            }
        }
    }
}
