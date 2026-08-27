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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.MarkChatRead
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.data.AppLanguage
import com.example.data.ChatMessage
import com.example.data.NegotiationOffer
import com.example.data.OfferStatus
import com.example.ui.components.OfferStatusBadge
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
fun OffersAndChatScreen(
    offers: List<NegotiationOffer>,
    chats: List<ChatMessage>,
    currentLanguage: AppLanguage,
    onRespondOffer: (offerId: String, accept: Boolean, counterPrice: Long?) -> Unit,
    onSendMessage: (String) -> Unit
) {
    val isArabic = currentLanguage == AppLanguage.ARABIC
    var selectedTabIdx by remember { mutableStateOf(0) }

    var counterDialogOffer by remember { mutableStateOf<NegotiationOffer?>(null) }
    var counterPriceInput by remember { mutableStateOf("") }

    var chatTextInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Header
        TabRow(
            selectedTabIndex = selectedTabIdx,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary
        ) {
            Tab(
                selected = selectedTabIdx == 0,
                onClick = { selectedTabIdx = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "عروض المساومة (${offers.size})" else "Offres de prix (${offers.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            )

            Tab(
                selected = selectedTabIdx == 1,
                onClick = { selectedTabIdx = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "الدردشة المباشرة" else "Messages directs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            )
        }

        if (selectedTabIdx == 0) {
            // Offers Tab
            if (offers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = null,
                            tint = SlateMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isArabic) "لا توجد عروض مساومة حالياً" else "Aucune offre de prix",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(offers, key = { it.id }) { offer ->
                        OfferCardItem(
                            offer = offer,
                            isArabic = isArabic,
                            onAccept = { onRespondOffer(offer.id, true, null) },
                            onReject = { onRespondOffer(offer.id, false, null) },
                            onCounter = {
                                counterDialogOffer = offer
                                counterPriceInput = ((offer.originalPriceDzd + offer.proposedPriceDzd) / 2).toString()
                            }
                        )
                    }
                }
            }
        } else {
            // Chat Tab
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chats, key = { it.id }) { msg ->
                        val isMe = msg.isFromMe
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                color = if (isMe) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(14.dp),
                                shadowElevation = 1.dp
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = msg.senderName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isMe) Color.White.copy(alpha = 0.8f) else EmeraldPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = msg.text,
                                        fontSize = 13.sp,
                                        color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = msg.timestamp,
                                        fontSize = 9.sp,
                                        color = if (isMe) Color.White.copy(alpha = 0.6f) else SlateMuted,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatTextInput,
                        onValueChange = { chatTextInput = it },
                        placeholder = {
                            Text(
                                text = if (isArabic) "اكتب رسالة فورية..." else "Écrire un message...",
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (chatTextInput.isNotBlank()) {
                                onSendMessage(chatTextInput)
                                chatTextInput = ""
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                            .size(46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // Counter Offer Dialog
    if (counterDialogOffer != null) {
        val targetOffer = counterDialogOffer!!
        AlertDialog(
            onDismissRequest = { counterDialogOffer = null },
            title = {
                Text(
                    text = if (isArabic) "تقديم عرض مضاد للبائع" else "Faire une contre-offre",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "الإعلان: ${targetOffer.listingTitle}",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "السعر الأصلي: ${formatPriceDzd(targetOffer.originalPriceDzd, isArabic)}",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "عرض المشتري: ${formatPriceDzd(targetOffer.proposedPriceDzd, isArabic)}",
                        fontSize = 12.sp,
                        color = AmberAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = counterPriceInput,
                        onValueChange = { counterPriceInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (isArabic) "سعرك المضاد (دج)" else "Votre contre-prix (DZD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val counterPrice = counterPriceInput.toLongOrNull()
                        onRespondOffer(targetOffer.id, false, counterPrice)
                        counterDialogOffer = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text(if (isArabic) "إرسال العرض المضاد" else "Envoyer la contre-offre")
                }
            },
            dismissButton = {
                TextButton(onClick = { counterDialogOffer = null }) {
                    Text(if (isArabic) "إلغاء" else "Annuler")
                }
            }
        )
    }
}

@Composable
fun OfferCardItem(
    offer: NegotiationOffer,
    isArabic: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onCounter: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Text(
                    text = offer.listingTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
                OfferStatusBadge(status = offer.status, isArabic = isArabic)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isArabic) "السعر المعروض:" else "Prix demandé :",
                        fontSize = 11.sp,
                        color = SlateMuted
                    )
                    Text(
                        text = formatPriceDzd(offer.originalPriceDzd, isArabic),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isArabic) "السومة المقترحة (العرض):" else "Offre proposée :",
                        fontSize = 11.sp,
                        color = AmberAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatPriceDzd(offer.proposedPriceDzd, isArabic),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = AmberAccent
                    )
                }
            }

            if (offer.counterPriceDzd != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isArabic) "العرض المضاد من البائع: ${formatPriceDzd(offer.counterPriceDzd, true)}"
                        else "Contre-offre du vendeur : ${formatPriceDzd(offer.counterPriceDzd, false)}",
                        color = OnEmeraldContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            if (offer.message.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "« ${offer.message} »",
                    fontSize = 12.sp,
                    color = SlateMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${offer.buyerName} (${offer.buyerPhone}) • ${offer.timestamp}",
                fontSize = 11.sp,
                color = SlateMuted
            )

            if (offer.status == OfferStatus.PENDING) {
                Spacer(modifier = Modifier.height(10.dp))
                Divider()
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isArabic) "قبول" else "Accepter", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onCounter,
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = if (isArabic) "عرض مضاد" else "Contre-offre", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = StatusRed)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isArabic) "رفض" else "Refuser", fontSize = 12.sp, color = StatusRed)
                    }
                }
            }
        }
    }
}
