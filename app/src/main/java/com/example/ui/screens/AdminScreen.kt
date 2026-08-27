package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import com.example.data.PaymentVerificationRecord
import com.example.data.PlatformSettings
import com.example.ui.components.AdStatusBadge
import com.example.ui.components.formatPriceDzd
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed

@Composable
fun AdminScreen(
    verifications: List<PaymentVerificationRecord>,
    platformSettings: PlatformSettings,
    currentLanguage: AppLanguage,
    onApprovePayment: (verificationId: String) -> Unit,
    onRejectPayment: (verificationId: String, reason: String) -> Unit
) {
    val isArabic = currentLanguage == AppLanguage.ARABIC

    val verifiedCount = verifications.count { it.status == AdStatus.PUBLISHED }
    val pendingCount = verifications.count { it.status == AdStatus.PAYMENT_PENDING }
    val totalRevenueDzd = verifiedCount * platformSettings.feeAmountDzd.toLong()

    var rejectDialogVerification by remember { mutableStateOf<PaymentVerificationRecord?>(null) }
    var rejectReasonText by remember { mutableStateOf("رقم العملية غير مطابق في كشف حساب بريدي موب") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Admin Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = AmberAccent,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (isArabic) "لوحة إدارة ومراقبة مدفوعات CCP" else "Contrôle des Paiements CCP / BaridiMob",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isArabic) "التحقق من رسوم النشر 200 دج وتفعيل الإعلانات" else "Validation des frais 200 DZD et publication",
                    fontSize = 11.sp,
                    color = SlateMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Revenue & Stats Metrics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Metric 1: Revenue
            Card(
                modifier = Modifier.weight(1.2f),
                colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = OnEmeraldContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "إيرادات رسوم النشر" else "Revenus des frais",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnEmeraldContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = formatPriceDzd(totalRevenueDzd, isArabic),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = OnEmeraldContainer
                    )
                    Text(
                        text = "$verifiedCount × 200 دج",
                        fontSize = 10.sp,
                        color = EmeraldPrimary
                    )
                }
            }

            // Metric 2: Pending
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = AmberContainer),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PendingActions,
                            contentDescription = null,
                            tint = OnAmberContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "قيد المراجعة" else "En attente",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnAmberContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$pendingCount",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = OnAmberContainer
                    )
                    Text(
                        text = if (isArabic) "إيصال جديد" else "dossiers",
                        fontSize = 10.sp,
                        color = AmberAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (isArabic) "قائمة إثباتات الدفع والحوالات (200 دج)" else "Dossiers de paiement 200 DZD",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (verifications.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isArabic) "لا توجد طلبات تحقق حالياً" else "Aucun dossier en attente",
                    fontSize = 13.sp,
                    color = SlateMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(verifications, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.listingTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = EmeraldPrimary
                                    )
                                    Text(
                                        text = "${item.sellerName} (${item.sellerPhone}) • ${item.wilaya}",
                                        fontSize = 11.sp,
                                        color = SlateMuted
                                    )
                                }
                                AdStatusBadge(status = item.status, isArabic = isArabic)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "رقم العملية (Référence):",
                                            fontSize = 10.sp,
                                            color = SlateMuted
                                        )
                                        Text(
                                            text = item.transactionRef,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "المبلغ:",
                                            fontSize = 10.sp,
                                            color = SlateMuted
                                        )
                                        Text(
                                            text = "200 دج",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = StatusGreen
                                        )
                                    }
                                }
                            }

                            if (item.status == AdStatus.PAYMENT_PENDING) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onApprovePayment(item.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1.2f)
                                            .testTag("admin_approve_${item.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isArabic) "تأكيد الدفع ونشر الإعلان" else "Valider le paiement",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { rejectDialogVerification = item },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(0.8f)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = StatusRed)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isArabic) "رفض" else "Refuser",
                                            fontSize = 12.sp,
                                            color = StatusRed
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

    // Rejection Dialog
    if (rejectDialogVerification != null) {
        val targetVerif = rejectDialogVerification!!
        AlertDialog(
            onDismissRequest = { rejectDialogVerification = null },
            title = {
                Text(
                    text = if (isArabic) "رفض إيصال الدفع" else "Refuser le paiement",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "الإعلان: ${targetVerif.listingTitle}",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReasonText,
                        onValueChange = { rejectReasonText = it },
                        label = { Text(if (isArabic) "سبب الرفض (يظهر للمعلن)" else "Motif de refus") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRejectPayment(targetVerif.id, rejectReasonText)
                        rejectDialogVerification = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                ) {
                    Text(if (isArabic) "تأكيد الرفض" else "Confirmer le refus")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectDialogVerification = null }) {
                    Text(if (isArabic) "إلغاء" else "Annuler")
                }
            }
        )
    }
}
