package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.CategoryType
import com.example.data.ListingItem
import com.example.data.Wilaya
import com.example.data.WilayasData
import com.example.ui.FilterState
import com.example.ui.components.AdStatusBadge
import com.example.ui.components.NegotiableBadge
import com.example.ui.components.WilayaBadge
import com.example.ui.components.formatPriceDzd
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.StatusGreen

@Composable
fun HomeScreen(
    listings: List<ListingItem>,
    filterState: FilterState,
    favoriteIds: Set<String>,
    currentLanguage: AppLanguage,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (CategoryType?) -> Unit,
    onWilayaSelect: (String?) -> Unit,
    onToggleNegotiable: () -> Unit,
    onResetFilters: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onListingClick: (ListingItem) -> Unit,
    onPostAdClick: () -> Unit
) {
    val isArabic = currentLanguage == AppLanguage.ARABIC
    var showWilayaDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & Wilaya Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = filterState.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("home_search_input"),
                placeholder = {
                    Text(
                        text = if (isArabic) "ابحث عن سيارة، هاتف، عقار..." else "Chercher un article...",
                        fontSize = 13.sp,
                        color = SlateMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = EmeraldPrimary
                    )
                },
                trailingIcon = {
                    if (filterState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Wilaya Selector Button (69 Wilayas)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (filterState.selectedWilayaCode != null) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showWilayaDialog = true }
                    .testTag("wilaya_filter_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Wilayas",
                        tint = if (filterState.selectedWilayaCode != null) Color.White else EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val wilayaLabel = filterState.selectedWilayaCode?.let { code ->
                        val w = WilayasData.findWilayaByCode(code)
                        if (isArabic) w?.nameAr else w?.nameFr
                    } ?: (if (isArabic) "69 ولاية" else "69 Wilayas")

                    Text(
                        text = wilayaLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (filterState.selectedWilayaCode != null) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Category Pills Carousel
        CategoryPillsRow(
            selectedCategory = filterState.selectedCategory,
            isArabic = isArabic,
            onCategorySelect = onCategorySelect
        )

        // Filter Pills Row (Negotiable toggle + Clear filter badge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = filterState.onlyNegotiable,
                onClick = onToggleNegotiable,
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "قابل للمساومة فقط" else "Négociable uniquement",
                            fontSize = 12.sp
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmeraldContainer,
                    selectedLabelColor = OnEmeraldContainer
                )
            )

            if (filterState.selectedCategory != null || filterState.selectedWilayaCode != null || filterState.onlyNegotiable || filterState.searchQuery.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onResetFilters() }
                ) {
                    Text(
                        text = if (isArabic) "إعادة ضبط الفلاتر ✕" else "Réinitialiser ✕",
                        fontSize = 11.sp,
                        color = AmberAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            AnimatedContent(
                targetState = listings.size,
                transitionSpec = {
                    (slideInVertically { it / 2 } + fadeIn(tween(200))).togetherWith(
                        slideOutVertically { -it / 2 } + fadeOut(tween(180))
                    )
                },
                label = "listing_count_anim"
            ) { count ->
                Text(
                    text = "$count ${if (isArabic) "إعلان متاح" else "annonces"}",
                    fontSize = 12.sp,
                    color = SlateMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Listings List
        if (listings.isEmpty()) {
            EmptyListingsView(isArabic = isArabic, onResetFilters = onResetFilters)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listings, key = { it.id }) { item ->
                    Box(modifier = Modifier.animateItem()) {
                        AdCardItem(
                            item = item,
                            isFavorite = favoriteIds.contains(item.id),
                            isArabic = isArabic,
                            onToggleFavorite = { onToggleFavorite(item.id) },
                            onClick = { onListingClick(item) }
                        )
                    }
                }
            }
        }
    }

    // 69 Wilayas Dialog
    if (showWilayaDialog) {
        WilayaSelectionDialog(
            currentWilayaCode = filterState.selectedWilayaCode,
            isArabic = isArabic,
            onDismiss = { showWilayaDialog = false },
            onSelectWilaya = { code ->
                onWilayaSelect(code)
                showWilayaDialog = false
            }
        )
    }
}

@Composable
fun CategoryPillsRow(
    selectedCategory: CategoryType?,
    isArabic: Boolean,
    onCategorySelect: (CategoryType?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            val isAllSelected = selectedCategory == null
            val bgColor by animateColorAsState(
                targetValue = if (isAllSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                animationSpec = tween(220),
                label = "all_bg_color"
            )
            val textColor by animateColorAsState(
                targetValue = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                animationSpec = tween(220),
                label = "all_text_color"
            )
            val iconColor by animateColorAsState(
                targetValue = if (isAllSelected) Color.White else EmeraldPrimary,
                animationSpec = tween(220),
                label = "all_icon_color"
            )
            val pillScale by animateFloatAsState(
                targetValue = if (isAllSelected) 1.04f else 1.0f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "all_pill_scale"
            )
            val elevation by animateDpAsState(
                targetValue = if (isAllSelected) 3.dp else 0.5.dp,
                animationSpec = tween(220),
                label = "all_pill_elevation"
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = bgColor,
                shadowElevation = elevation,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = pillScale
                        scaleY = pillScale
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onCategorySelect(null) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "الكل" else "Tout",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }

        items(CategoryType.values()) { category ->
            val isSelected = selectedCategory == category
            val icon = getCategoryIcon(category)
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                animationSpec = tween(220),
                label = "cat_bg_color"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                animationSpec = tween(220),
                label = "cat_text_color"
            )
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else EmeraldPrimary,
                animationSpec = tween(220),
                label = "cat_icon_color"
            )
            val pillScale by animateFloatAsState(
                targetValue = if (isSelected) 1.04f else 1.0f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "cat_pill_scale"
            )
            val elevation by animateDpAsState(
                targetValue = if (isSelected) 3.dp else 0.5.dp,
                animationSpec = tween(220),
                label = "cat_pill_elevation"
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = bgColor,
                shadowElevation = elevation,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = pillScale
                        scaleY = pillScale
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onCategorySelect(category) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) category.titleAr else category.titleFr,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun AdCardItem(
    item: ListingItem,
    isFavorite: Boolean,
    isArabic: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val favoriteScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.3f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fav_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("ad_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category Badge, Wilaya Badge & Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    WilayaBadge(code = item.wilayaCode, name = if (isArabic) item.wilayaNameAr else item.wilayaNameFr)
                    if (item.isNegotiable) {
                        NegotiableBadge(isArabic = isArabic)
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) AmberAccent else SlateMuted,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer {
                                scaleX = favoriteScale
                                scaleY = favoriteScale
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Description preview
            Text(
                text = item.description,
                fontSize = 12.sp,
                color = SlateMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Row: Price & Meta
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = formatPriceDzd(item.priceDzd, isArabic),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldPrimary
                    )
                    Text(
                        text = "${item.commune} • ${item.createdAt}",
                        fontSize = 11.sp,
                        color = SlateMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (item.offersCount > 0) {
                        Surface(
                            color = AmberContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Handshake,
                                    contentDescription = null,
                                    tint = OnAmberContainer,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${item.offersCount} ${if (isArabic) "سومة" else "offres"}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnAmberContainer
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = SlateMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${item.viewsCount}",
                            fontSize = 11.sp,
                            color = SlateMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WilayaSelectionDialog(
    currentWilayaCode: String?,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onSelectWilaya: (String?) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredWilayas = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            WilayasData.allWilayas
        } else {
            WilayasData.allWilayas.filter {
                it.code.contains(searchQuery) ||
                        it.nameAr.contains(searchQuery, ignoreCase = true) ||
                        it.nameFr.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (isArabic) "اختر الولاية (69 ولاية جزائرية)" else "Sélectionner la wilaya (69 Wilayas)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isArabic) "ابحث برقم الولاية أو الاسم..." else "Code ou nom de wilaya...",
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectWilaya(null) }
                            .padding(vertical = 8.dp),
                        color = if (currentWilayaCode == null) EmeraldContainer else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isArabic) "كل ولايات الجزائر (69 ولاية)" else "Toute l'Algérie (69 Wilayas)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (currentWilayaCode == null) OnEmeraldContainer else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                items(filteredWilayas) { wilaya ->
                    val isSelected = wilaya.code == currentWilayaCode
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectWilaya(wilaya.code) }
                            .padding(vertical = 3.dp),
                        color = if (isSelected) EmeraldContainer else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = wilaya.code,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = EmeraldPrimary,
                                modifier = Modifier.width(30.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "${wilaya.nameAr} (${wilaya.nameFr})" else "${wilaya.nameFr} (${wilaya.nameAr})",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) OnEmeraldContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (isArabic) "إغلاق" else "Fermer")
            }
        }
    )
}

@Composable
fun EmptyListingsView(isArabic: Boolean, onResetFilters: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = SlateMuted,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isArabic) "لا توجد إعلانات مطابقة لبحثك" else "Aucune annonce trouvée",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isArabic) "جرب تغيير الولاية أو إعادة ضبط الفلاتر." else "Essayez de changer de wilaya ou réinitialiser les filtres.",
                fontSize = 12.sp,
                color = SlateMuted
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onResetFilters) {
                Text(text = if (isArabic) "إعادة ضبط كل الفلاتر" else "Réinitialiser les filtres")
            }
        }
    }
}

fun getCategoryIcon(category: CategoryType): ImageVector {
    return when (category) {
        CategoryType.VEHICLES -> Icons.Default.DirectionsCar
        CategoryType.REAL_ESTATE -> Icons.Default.Apartment
        CategoryType.PHONES_TECH -> Icons.Default.Smartphone
        CategoryType.HOME_APPLIANCES -> Icons.Default.Kitchen
        CategoryType.FASHION -> Icons.Default.Checkroom
        CategoryType.FURNITURE -> Icons.Default.Weekend
        CategoryType.TOOLS_INDUSTRY -> Icons.Default.Build
        CategoryType.SERVICES -> Icons.Default.Work
        CategoryType.OTHER -> Icons.Default.Category
    }
}
