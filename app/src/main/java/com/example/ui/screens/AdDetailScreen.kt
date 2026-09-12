package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AdStatus
import com.example.data.AppLanguage
import com.example.data.ChatMessage
import com.example.data.DeliveryOption
import com.example.data.ListingItem
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdDetailScreen(
    listing: ListingItem,
    isFavorite: Boolean,
    currentLanguage: AppLanguage,
    chats: List<ChatMessage>,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit,
    onSendOffer: (proposedPrice: Long, message: String) -> Unit,
    onSendMessage: (String) -> Unit
) {
    val isArabic = currentLanguage == AppLanguage.ARABIC
    val context = LocalContext.current

    var proposedPriceText by remember {
        mutableStateOf((listing.priceDzd * 0.95).toLong().toString())
    }
    var offerMessage by remember { mutableStateOf("") }
    var offerSubmittedFeedback by remember { mutableStateOf(false) }

    var chatInputText by remember { mutableStateOf("") }

    val listingChats = remember(chats, listing.id) {
        chats.filter { it.listingId == listing.id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            title = {
                Text(
                    text = if (isArabic) "تفاصيل الإعلان" else "Détails de l'annonce",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) AmberAccent else SlateMuted
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Visual Hero Card / Photos Gallery
            item {
                if (listing.images.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Main Photo
                        var selectedPhotoIndex by remember { mutableStateOf(0) }
                        val currentPhoto = listing.images.getOrElse(selectedPhotoIndex) { listing.images.first() }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            AsyncImage(
                                model = currentPhoto,
                                contentDescription = listing.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Image count pill
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "${selectedPhotoIndex + 1} / ${listing.images.size}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Thumbnail strip if more than 1 image
                        if (listing.images.size > 1) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(listing.images.size) { index ->
                                    val isCurrent = index == selectedPhotoIndex
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { selectedPhotoIndex = index }
                                            .then(
                                                if (isCurrent) Modifier.background(EmeraldPrimary)
                                                else Modifier
                                            )
                                            .padding(if (isCurrent) 2.dp else 0.dp)
                                    ) {
                                        AsyncImage(
                                            model = listing.images[index],
                                            contentDescription = "Thumbnail $index",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldContainer)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = getCategoryIcon(listing.category),
                                    contentDescription = null,
                                    tint = OnEmeraldContainer,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isArabic) listing.category.titleAr else listing.category.titleFr,
                                    fontWeight = FontWeight.Bold,
                                    color = OnEmeraldContainer,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${listing.commune} • ${if (isArabic) listing.wilayaNameAr else listing.wilayaNameFr}",
                                    color = EmeraldPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Title & Price Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WilayaBadge(
                                code = listing.wilayaCode,
                                name = if (isArabic) listing.wilayaNameAr else listing.wilayaNameFr
                            )
                            AdStatusBadge(status = listing.status, isArabic = isArabic)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = listing.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = formatPriceDzd(listing.priceDzd, isArabic),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldPrimary
                            )

                            if (listing.isNegotiable) {
                                NegotiableBadge(isArabic = isArabic)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isArabic) "الحالة: " else "État: ",
                                        fontSize = 11.sp,
                                        color = SlateMuted
                                    )
                                    Text(
                                        text = if (isArabic) listing.condition.labelAr else listing.condition.labelFr,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Surface(
                                color = EmeraldContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = OnEmeraldContainer,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) listing.deliveryOption.labelAr else listing.deliveryOption.labelFr,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnEmeraldContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isArabic) "وصف الإعلان" else "Description de l'article",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = listing.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Seller Contact Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = OnEmeraldContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = listing.sellerName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (listing.isSellerVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified Seller",
                                            tint = StatusGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${listing.commune} • ${listing.wilayaNameAr}",
                                    fontSize = 11.sp,
                                    color = SlateMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${listing.sellerPhone}")
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("call_seller_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${if (isArabic) "الاتصال بالبائع" else "Appeler"} (${listing.sellerPhone})",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Price Bargaining & Negotiation Box (المساومة)
            if (listing.isNegotiable) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AmberContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Handshake,
                                    contentDescription = null,
                                    tint = OnAmberContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "المساومة واقتراح سعر (Négociation)" else "Proposer un prix négocié",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnAmberContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isArabic) "السعر الأصلي: ${formatPriceDzd(listing.priceDzd, true)} - يمكنك اقتراح سعرك المباشر للبائع."
                                else "Prix affiché: ${formatPriceDzd(listing.priceDzd, false)}. Faites une offre au vendeur.",
                                fontSize = 12.sp,
                                color = OnAmberContainer.copy(alpha = 0.85f)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Discount presets (-5%, -10%, -15%)
                            Text(
                                text = if (isArabic) "اقتراحات سريعة للخصم:" else "Suggestions de réduction rapide:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnAmberContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(5, 10, 15).forEach { percent ->
                                    val discounted = (listing.priceDzd * (100 - percent) / 100)
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                proposedPriceText = discounted.toString()
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "-$percent%",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                color = AmberAccent
                                            )
                                            Text(
                                                text = formatPriceDzd(discounted, isArabic),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SlateMuted
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = proposedPriceText,
                                onValueChange = { proposedPriceText = it.filter { ch -> ch.isDigit() } },
                                label = { Text(if (isArabic) "سعرك المقترح بالدينار (دج)" else "Votre prix proposé (DZD)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("negotiation_price_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = offerMessage,
                                onValueChange = { offerMessage = it },
                                label = { Text(if (isArabic) "رسالة للبائع (اختياري)" else "Message au vendeur (optionnel)") },
                                placeholder = {
                                    Text(
                                        if (isArabic) "مثال: جاد وندفع كاش بعد المعاينة" else "Ex: Acheteur sérieux, paiement cash"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val price = proposedPriceText.toLongOrNull() ?: listing.priceDzd
                                    onSendOffer(price, offerMessage)
                                    offerSubmittedFeedback = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_offer_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isArabic) "إرسال عرض السعر للبائع 🤝" else "Envoyer l'offre de prix 🤝",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (offerSubmittedFeedback) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = EmeraldContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isArabic) "✓ تم إرسال عرضك بنجاح! يمكنك متابعة رد البائع في تبويب المساومة."
                                        else "✓ Offre transmise avec succès! Suivez la réponse dans l'onglet Négociations.",
                                        color = OnEmeraldContainer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Direct In-App Chat Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "الدردشة المباشرة مع البائع" else "Discussion instantanée",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick pre-filled phrases
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val quickReplies = if (isArabic) listOf(
                                "هل السعر قابل للنقاش؟",
                                "أين يمكن المعاينة؟",
                                "هل التوصيل متوفر لولايتي؟",
                                "هل المنتج مازال متوفر؟"
                            ) else listOf(
                                "Prix discutable ?",
                                "Où voir l'article ?",
                                "Livraison disponible ?",
                                "Toujours disponible ?"
                            )

                            items(quickReplies) { reply ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { chatInputText = reply }
                                ) {
                                    Text(
                                        text = reply,
                                        fontSize = 11.sp,
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Chat Messages List
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (listingChats.isEmpty()) {
                                Text(
                                    text = if (isArabic) "ابدأ المحادثة مع البائع الآن..." else "Commencez la discussion...",
                                    fontSize = 12.sp,
                                    color = SlateMuted,
                                    modifier = Modifier.padding(8.dp)
                                )
                            } else {
                                listingChats.forEach { msg ->
                                    val isMe = msg.isFromMe
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                                    ) {
                                        Surface(
                                            color = if (isMe) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = msg.senderName,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isMe) Color.White.copy(alpha = 0.8f) else SlateMuted
                                                )
                                                Text(
                                                    text = msg.text,
                                                    fontSize = 12.sp,
                                                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Chat Input Bar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = chatInputText,
                                onValueChange = { chatInputText = it },
                                placeholder = {
                                    Text(
                                        if (isArabic) "اكتب رسالة للبائع..." else "Votre message...",
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_input_field"),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    if (chatInputText.isNotBlank()) {
                                        onSendMessage(chatInputText)
                                        chatInputText = ""
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                                    .size(44.dp)
                                    .testTag("chat_send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
