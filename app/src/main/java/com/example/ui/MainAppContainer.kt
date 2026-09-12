package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.data.AdStatus
import com.example.data.AppLanguage
import com.example.data.OfferStatus
import com.example.ui.components.AchriDZTopBar
import com.example.ui.components.AuthDialog
import com.example.ui.screens.AdDetailScreen
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
    val myAds by viewModel.myAds.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()
    val showAuthDialog by viewModel.showAuthDialog.collectAsState()
    val visitorCount by viewModel.visitorCount.collectAsState()
    var showAccountDialog by remember { mutableStateOf(false) }

    // Fluctuates visitor count every 5 seconds ("والعدد يتغير كل 5ثواني")
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000L)
            viewModel.updateVisitorCount()
        }
    }

    val isArabic = currentLanguage == AppLanguage.ARABIC
    val pendingOffersCount = offers.count { it.status == OfferStatus.PENDING }

    if (showAuthDialog) {
        AuthDialog(
            userAccount = userAccount,
            currentLanguage = currentLanguage,
            onDismiss = { viewModel.closeAuthDialog() },
            onLogin = { phone, pass, role -> viewModel.login(phone, pass, role) },
            onRegister = { name, email, password, wilaya, role -> viewModel.register(name, email, password, wilaya, role) }
        )
    }

    if (showAccountDialog && userAccount.isLoggedIn) {
        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            title = { Text(if (isArabic) "بياناتي" else "Mes données") },
            text = {
                Text(
                    if (isArabic) {
                        "الاسم: ${userAccount.name}\nالهاتف: ${userAccount.phone}\nالولاية: ${userAccount.wilayaCode}"
                    } else {
                        "Nom: ${userAccount.name}\nTéléphone: ${userAccount.phone}\nWilaya: ${userAccount.wilayaCode}"
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showAccountDialog = false
                    viewModel.logout()
                }) {
                    Text(if (isArabic) "تسجيل الخروج" else "Se déconnecter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    Text(if (isArabic) "إغلاق" else "Fermer")
                }
            }
        )
    }

    AnimatedContent(
        targetState = selectedListing,
        transitionSpec = {
            if (targetState != null) {
                (slideInVertically(
                    initialOffsetY = { it / 6 },
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                ) + fadeIn(animationSpec = tween(240))).togetherWith(
                    fadeOut(animationSpec = tween(180))
                )
            } else {
                fadeIn(animationSpec = tween(200)).togetherWith(
                    slideOutVertically(
                        targetOffsetY = { it / 6 },
                        animationSpec = tween(220)
                    ) + fadeOut(animationSpec = tween(180))
                )
            }
        },
        label = "listing_detail_screen_transition"
    ) { currentSelectedListing ->
        if (currentSelectedListing != null) {
            AdDetailScreen(
                listing = currentSelectedListing,
                isFavorite = favoriteIds.contains(currentSelectedListing.id),
                currentLanguage = currentLanguage,
                chats = chats,
                onToggleFavorite = { viewModel.toggleFavorite(currentSelectedListing.id) },
                onBack = { viewModel.selectListing(null) },
                onSendOffer = { price, msg ->
                    viewModel.sendOffer(currentSelectedListing, price, msg)
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
                        currentLanguage = currentLanguage,
                        visitorCount = visitorCount,
                        isLoggedIn = userAccount.isLoggedIn,
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onOpenAuth = { viewModel.openAuthDialog() },
                        onOpenAccount = { showAccountDialog = true }
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
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            val forward = targetState.ordinal > initialState.ordinal
                            val multiplier = if (isArabic) -1 else 1
                            val direction = if (forward) multiplier else -multiplier
                            (slideInHorizontally(
                                initialOffsetX = { (it * 0.12f * direction).toInt() },
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                            ) + fadeIn(animationSpec = tween(220))).togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { (-it * 0.12f * direction).toInt() },
                                    animationSpec = tween(180)
                                ) + fadeOut(animationSpec = tween(180))
                            )
                        },
                        label = "tab_content_transition",
                        modifier = Modifier.fillMaxSize()
                    ) { currentTab ->
                        when (currentTab) {
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
                                    },
                                    onUploadReceipt = { viewModel.uploadReceipt(it) },
                                    onRemoveReceipt = { viewModel.removeReceipt() }
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
                        }
                    }
                }
            }
        }
    }
}
