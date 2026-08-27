package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdStatus
import com.example.data.AppLanguage
import com.example.data.CategoryType
import com.example.data.OfferStatus
import com.example.data.UserRole
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberContainer
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer
import java.text.NumberFormat
import java.util.Locale

fun formatPriceDzd(amount: Long, isArabic: Boolean = true): String {
    val formatted = NumberFormat.getNumberInstance(Locale.FRANCE).format(amount)
    return if (isArabic) "$formatted دج" else "$formatted DZD"
}

@Composable
fun AdStatusBadge(status: AdStatus, isArabic: Boolean = true) {
    val (bgColor, textColor, text) = when (status) {
        AdStatus.PUBLISHED -> Triple(
            StatusGreenContainer,
            StatusGreen,
            if (isArabic) "منشور في السوق" else "Publié"
        )
        AdStatus.PAYMENT_PENDING -> Triple(
            StatusAmberContainer,
            StatusAmber,
            if (isArabic) "قيد مراجعة 200 دج" else "Vérif CCP 200 DZD"
        )
        AdStatus.PAYMENT_REQUIRED -> Triple(
            AmberContainer,
            OnAmberContainer,
            if (isArabic) "يتطلب دفع 200 دج" else "Paiement requis"
        )
        AdStatus.REJECTED -> Triple(
            StatusRedContainer,
            StatusRed,
            if (isArabic) "مرفوض" else "Rejeté"
        )
        AdStatus.SOLD -> Triple(
            Color(0xFFE2E8F0),
            Color(0xFF475569),
            if (isArabic) "تم البيع" else "Vendu"
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun OfferStatusBadge(status: OfferStatus, isArabic: Boolean = true) {
    val (bgColor, textColor, text) = when (status) {
        OfferStatus.ACCEPTED -> Triple(
            StatusGreenContainer,
            StatusGreen,
            if (isArabic) "مقبول" else "Acceptée"
        )
        OfferStatus.PENDING -> Triple(
            StatusAmberContainer,
            StatusAmber,
            if (isArabic) "قيد الانتظار" else "En attente"
        )
        OfferStatus.COUNTER_OFFER -> Triple(
            EmeraldContainer,
            OnEmeraldContainer,
            if (isArabic) "عرض مضاد" else "Contre-offre"
        )
        OfferStatus.REJECTED -> Triple(
            StatusRedContainer,
            StatusRed,
            if (isArabic) "مرفوض" else "Refusée"
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun WilayaBadge(code: String, name: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "$code - $name",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NegotiableBadge(isArabic: Boolean = true) {
    Surface(
        color = EmeraldContainer,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Handshake,
                contentDescription = null,
                tint = OnEmeraldContainer,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = if (isArabic) "قابل للمساومة" else "Négociable",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = OnEmeraldContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchriDZTopBar(
    currentRole: UserRole,
    currentLanguage: AppLanguage,
    onRoleSelected: (UserRole) -> Unit,
    onToggleLanguage: () -> Unit
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }
    val isArabic = currentLanguage == AppLanguage.ARABIC

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "AchriDZ Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AchriDZ",
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            color = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "🇩🇿",
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = if (isArabic) "سوق المستعمل 69 ولاية" else "Marketplace 69 Wilayas",
                        fontSize = 11.sp,
                        color = SlateMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        actions = {
            // Language switch button
            IconButton(
                onClick = onToggleLanguage,
                modifier = Modifier.testTag("lang_toggle_button")
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "FR" else "عربي",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Role Switcher Dropdown
            Box {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (currentRole == UserRole.ADMIN) AmberContainer else EmeraldContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { roleMenuExpanded = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("role_switcher_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = when (currentRole) {
                                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                UserRole.SELLER -> Icons.Default.Storefront
                                UserRole.BUYER -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = if (currentRole == UserRole.ADMIN) OnAmberContainer else OnEmeraldContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) currentRole.displayNameAr.split(" ")[0] else currentRole.displayNameFr.split(" ")[0],
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentRole == UserRole.ADMIN) OnAmberContainer else OnEmeraldContainer
                        )
                    }
                }

                DropdownMenu(
                    expanded = roleMenuExpanded,
                    onDismissRequest = { roleMenuExpanded = false }
                ) {
                    UserRole.values().forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (role) {
                                            UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                            UserRole.SELLER -> Icons.Default.Storefront
                                            UserRole.BUYER -> Icons.Default.Person
                                        },
                                        contentDescription = null,
                                        tint = if (role == currentRole) EmeraldPrimary else SlateMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isArabic) role.displayNameAr else role.displayNameFr,
                                        fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (role == currentRole) {
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onRoleSelected(role)
                                roleMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}
