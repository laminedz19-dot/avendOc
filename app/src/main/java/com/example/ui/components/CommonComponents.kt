package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.alpha
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
            if (isArabic) "في انتظار مصادقة الإدارة" else "En attente de validation"
        )
        AdStatus.PAYMENT_REQUIRED -> Triple(
            AmberContainer,
            OnAmberContainer,
            if (isArabic) "يتطلب دفع 300 دج" else "Paiement requis"
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
    currentLanguage: AppLanguage,
    visitorCount: Int = 18450,
    currentRole: UserRole = UserRole.SELLER,
    isLoggedIn: Boolean = false,
    onRoleSelected: (UserRole) -> Unit = {},
    onToggleLanguage: () -> Unit,
    onOpenAuth: () -> Unit = {},
    onOpenAccount: () -> Unit = {}
) {
    val isArabic = currentLanguage == AppLanguage.ARABIC

    // Blinking animation every 3 seconds (1500ms down + 1500ms up = 3000ms complete cycle)
    val infiniteTransition = rememberInfiniteTransition(label = "visitor_blink_transition")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "visitor_blink_alpha"
    )

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
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "AchriDZ Logo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AchriDZ",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "🇩🇿",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // The sentence next to AchriDZ: "عدد زوارنا الآن [عدد] زائر", blinking every 3 seconds
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldContainer,
                            modifier = Modifier
                                .alpha(blinkAlpha)
                                .testTag("visitor_count_badge")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(StatusGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "عدد زوارنا الآن $visitorCount زائر" else "Visiteurs actuels: $visitorCount",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnEmeraldContainer,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isArabic) "سوق المستعمل 69 ولاية" else "Marketplace 69 Wilayas",
                        fontSize = 10.sp,
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
                modifier = Modifier
                    .size(36.dp)
                    .testTag("lang_toggle_button")
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (isArabic) "FR" else "عربي",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Auth / Account button: login for guests, account data for logged-in users.
            IconButton(
                onClick = if (isLoggedIn) onOpenAccount else onOpenAuth,
                modifier = Modifier
                    .size(36.dp)
                    .testTag(if (isLoggedIn) "open_account_button" else "open_auth_button")
            ) {
                Surface(
                    shape = CircleShape,
                    color = EmeraldContainer,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = if (isLoggedIn) "بياناتي" else "تسجيل الدخول",
                            tint = OnEmeraldContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    )
}
