package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdStatus
import com.example.data.AppLanguage
import com.example.data.ListingItem
import com.example.data.PlatformSettings
import com.example.ui.components.AdStatusBadge
import com.example.ui.components.WilayaBadge
import com.example.ui.components.formatPriceDzd
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.StatusGreen

@Composable
fun MyAdsScreen(
    myAds: List<ListingItem>,
    platformSettings: PlatformSettings,
    currentLanguage: AppLanguage,
    onPostAdClick: () -> Unit,
    onSubmitPaymentForAd: (listingId: String, reference: String, date: String) -> Unit,
    onMarkAsSold: (listingId: String) -> Unit,
    onAdClick: (ListingItem) -> Unit
) {
    val isArabic = currentLanguage == AppLanguage.ARABIC
    var selectedStatusFilter by remember { mutableStateOf<AdStatus?>(null) }

    var paymentDialogAd by remember { mutableStateOf<ListingItem?>(null) }
    var paymentRefInput by remember { mutableStateOf("") }

    val filteredAds = remember(myAds, selectedStatusFilter) {
        if (selectedStatusFilter == null) myAds else myAds.filter { it.status == selectedStatusFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isArabic) "إعلاناتي وعمليات النشر" else "Mes annonces",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldPrimary
                )
                Text(
                    text = "${myAds.size} ${if (isArabic) "إعلان مسجل في حسابك" else "annonces dans votre compte"}",
                    fontSize = 12.sp,
                    color = SlateMuted
                )
            }

            Button(
                onClick = onPostAdClick,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("my_ads_post_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isArabic) "نشر إعلان" else "Publier", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text(if (isArabic) "الكل (${myAds.size})" else "Tous (${myAds.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldContainer,
                        selectedLabelColor = OnEmeraldContainer
                    )
                )
            }

            items(AdStatus.values()) { status ->
                val count = myAds.count { it.status == status }
                FilterChip(
                    selected = selectedStatusFilter == status,
                    onClick = { selectedStatusFilter = if (selectedStatusFilter == status) null else status },
                    label = {
                        Text(
                            text = "${if (isArabic) status.labelAr else status.labelFr} ($count)",
                            fontSize = 11.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldContainer,
                        selectedLabelColor = OnEmeraldContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredAds.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PostAdd,
                        contentDescription = null,
                        tint = SlateMuted,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isArabic) "لا توجد إعلانات بهذه الحالة" else "Aucune annonce trouvée",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredAds, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onAdClick(item) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                WilayaBadge(
                                    code = item.wilayaCode,
                                    name = if (isArabic) item.wilayaNameAr else item.wilayaNameFr
                                )
                                AdStatusBadge(status = item.status, isArabic = isArabic)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = formatPriceDzd(item.priceDzd, isArabic),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = EmeraldPrimary
                            )

                            if (item.paymentReference.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "رقم حوالة 200 دج: ${item.paymentReference}",
                                    fontSize = 11.sp,
                                    color = SlateMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons per status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (item.status == AdStatus.PAYMENT_REQUIRED || item.status == AdStatus.REJECTED) {
                                    Button(
                                        onClick = {
                                            paymentDialogAd = item
                                            paymentRefInput = "BM-2026-${(10000..99999).random()}"
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isArabic) "إدخال وصل 200 دج لتفعيل الإعلان" else "Payer 200 DZD pour publier",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else if (item.status == AdStatus.PUBLISHED) {
                                    OutlinedButton(
                                        onClick = { onMarkAsSold(item.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = if (isArabic) "تعليم كـ مُباع" else "Marquer comme vendu", fontSize = 12.sp)
                                    }
                                } else if (item.status == AdStatus.PAYMENT_PENDING) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (isArabic) "⏳ الإعلان قيد مراجعة الإدارة ولن يظهر في السوق حتى الموافقة" else "⏳ Validation administrative en cours",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SlateMuted,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Payment Proof Submission Dialog
    if (paymentDialogAd != null) {
        val targetAd = paymentDialogAd!!
        AlertDialog(
            onDismissRequest = { paymentDialogAd = null },
            title = {
                Text(
                    text = if (isArabic) "دفع رسوم النشر 300 دج" else "Paiement des frais 300 DZD",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "الإعلان: ${targetAd.title}",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = EmeraldContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "حساب CCP: ${platformSettings.ccpAccount} Clé ${platformSettings.ccpKey}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = OnEmeraldContainer
                            )
                            Text(
                                text = "بريدي موب RIP: ${platformSettings.baridiMobRip}",
                                fontSize = 11.sp,
                                color = OnEmeraldContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = paymentRefInput,
                        onValueChange = { paymentRefInput = it },
                        label = { Text(if (isArabic) "رقم الحوالة أو العملية" else "Référence de transaction") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmitPaymentForAd(targetAd.id, paymentRefInput, "2026-08-27")
                        paymentDialogAd = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text(if (isArabic) "إرسال للمراجعة" else "Envoyer pour vérification")
                }
            },
            dismissButton = {
                TextButton(onClick = { paymentDialogAd = null }) {
                    Text(if (isArabic) "إلغاء" else "Annuler")
                }
            }
        )
    }
}
