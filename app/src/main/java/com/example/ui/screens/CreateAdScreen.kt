package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.CategoryType
import com.example.data.ItemCondition
import com.example.data.PlatformSettings
import com.example.data.Wilaya
import com.example.data.WilayasData
import com.example.ui.CreateAdFormState
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
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen

@Composable
fun CreateAdScreen(
    formState: CreateAdFormState,
    platformSettings: PlatformSettings,
    currentLanguage: AppLanguage,
    onFormChange: ((CreateAdFormState) -> CreateAdFormState) -> Unit,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    onViewMyAds: () -> Unit
) {
    val isArabic = currentLanguage == AppLanguage.ARABIC
    val context = LocalContext.current

    val stepTitlesAr = listOf(
        "1. اختيار الفئة",
        "2. العنوان والوصف",
        "3. السعر والتفاوض",
        "4. حالة المنتج",
        "5. الولاية والبلدية (69 ولاية)",
        "6. الصور والمعاينة",
        "7. بيانات الاتصال",
        "8. خيارات التوصيل",
        "9. مراجعة الإعلان",
        "10. دفع رسوم النشر 200 دج"
    )

    val stepTitlesFr = listOf(
        "1. Catégorie",
        "2. Titre & Description",
        "3. Prix & Négociation",
        "4. État de l'article",
        "5. Wilaya & Commune (69 Wilayas)",
        "6. Photos",
        "7. Contact",
        "8. Livraison",
        "9. Récapitulatif",
        "10. Frais de publication 200 DZD"
    )

    val currentTitle = if (isArabic) stepTitlesAr[formState.currentStep - 1] else stepTitlesFr[formState.currentStep - 1]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Header with Progress Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isArabic) "معالج إنشاء الإعلان" else "Assistant de publication",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldPrimary
                )
                Text(
                    text = "$currentTitle (${formState.currentStep}/${formState.totalSteps})",
                    fontSize = 12.sp,
                    color = SlateMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            Surface(
                color = EmeraldContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${(formState.currentStep * 10)}%",
                    color = OnEmeraldContainer,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = { formState.currentStep.toFloat() / formState.totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = EmeraldPrimary,
            trackColor = EmeraldContainer
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Multi-Step Content Body
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (formState.currentStep) {
                1 -> item { Step1Category(formState, isArabic, onFormChange) }
                2 -> item { Step2TitleDescription(formState, isArabic, onFormChange) }
                3 -> item { Step3PriceNegotiation(formState, isArabic, onFormChange) }
                4 -> item { Step4Condition(formState, isArabic, onFormChange) }
                5 -> item { Step5Location(formState, isArabic, onFormChange) }
                6 -> item { Step6Photos(formState, isArabic) }
                7 -> item { Step7Contact(formState, isArabic, onFormChange) }
                8 -> item { Step8Delivery(formState, isArabic) }
                9 -> item { Step9Review(formState, isArabic) }
                10 -> item {
                    Step10Payment200Dzd(
                        formState = formState,
                        platformSettings = platformSettings,
                        isArabic = isArabic,
                        context = context,
                        onFormChange = onFormChange,
                        onSubmit = onSubmit,
                        onViewMyAds = onViewMyAds
                    )
                }
            }
        }

        // Stepper Navigation Footer (Previous / Next / Submit)
        if (formState.submittedAdId == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (formState.currentStep > 1) {
                    OutlinedButton(
                        onClick = onPrevStep,
                        modifier = Modifier.testTag("prev_step_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isArabic) "السابق" else "Précédent")
                    }
                } else {
                    Spacer(modifier = Modifier.width(10.dp))
                }

                if (formState.currentStep < formState.totalSteps) {
                    Button(
                        onClick = onNextStep,
                        modifier = Modifier.testTag("next_step_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isArabic) "التالي" else "Suivant",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Step1Category(
    formState: CreateAdFormState,
    isArabic: Boolean,
    onFormChange: ((CreateAdFormState) -> CreateAdFormState) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isArabic) "اختر فئة الإعلان" else "Choisissez la catégorie",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            CategoryType.values().forEach { cat ->
                val isSelected = formState.category == cat
                Surface(
                    color = if (isSelected) EmeraldContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onFormChange { it.copy(category = cat) } }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(cat),
                            contentDescription = null,
                            tint = if (isSelected) OnEmeraldContainer else SlateMuted
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isArabic) cat.titleAr else cat.titleFr,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) OnEmeraldContainer else MaterialTheme.colorScheme.onSurface
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = OnEmeraldContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Step2TitleDescription(
    formState: CreateAdFormState,
    isArabic: Boolean,
    onFormChange: ((CreateAdFormState) -> CreateAdFormState) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isArabic) "عنوان الإعلان وتفاصيله" else "Titre et description",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.title,
                onValueChange = { title -> onFormChange { it.copy(title = title) } },
                label = { Text(if (isArabic) "عنوان الإعلان (واضح وجذاب)" else "Titre de l'annonce") },
                placeholder = { Text(if (isArabic) "مثال: رونو كليو 4 سنة 2019 نقية" else "Ex: Renault Clio 4 2019") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_ad_title_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.description,
                onValueChange = { desc -> onFormChange { it.copy(description = desc) } },
                label = { Text(if (isArabic) "الوصف الدقيق للمنتج" else "Description détaillée") },
                placeholder = {
                    Text(
                        if (isArabic) "اذكر حالة المنتج، العيوب إن وجدت، سبب البيع، وإمكانية المعاينة..."
                        else "Détaillez l'état, défauts éventuels, motif de vente..."
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .testTag("create_ad_desc_input"),
                maxLines = 6,
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
fun Step3PriceNegotiation(
    formState: CreateAdFormState,
    isArabic: Boolean,
    onFormChange: ((CreateAdFormState) -> CreateAdFormState) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isArabic) "السعر وخاصية المساومة (التفاوض)" else "Prix et négociabilité",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.priceText,
                onValueChange = { p -> onFormChange { it.copy(priceText = p.filter { ch -> ch.isDigit() }) } },
                label = { Text(if (isArabic) "السعر المطلوب بالدينار الجزائري (دج)" else "Prix en Dinar Algérien (DZD)") },
                placeholder = { Text("45000") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_ad_price_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = EmeraldContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "السماح بالمساومة (Négociable)" else "Autoriser les négociations",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = OnEmeraldContainer
                        )
                        Text(
                            text = if (isArabic) "يسمح للمشترين باقتراح أسعار مختلفة وأنت تملك حق القبول أو الرفض."
                            else "Permet aux acheteurs de faire des contre-propositions de prix.",
                            fontSize = 11.sp,
                            color = OnEmeraldContainer.copy(alpha = 0.8f)
                        )
                    }

                    Switch(
                        checked = formState.isNegotiable,
                        onCheckedChange = { neg -> onFormChange { it.copy(isNegotiable = neg) } },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EmeraldPrimary,
                            checkedTrackColor = EmeraldLight
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun Step4Condition(
    formState: CreateAdFormState,
    isArabic: Boolean,
    onFormChange: ((CreateAdFormState) -> CreateAdFormState) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isArabic) "ما هي حالة المنتج؟" else "Quel est l'état de l'article ?",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemCondition.values().forEach { cond ->
                val isSelected = formState.condition == cond
                Surface(
                    color = if (isSelected) EmeraldContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onFormChange { it.copy(condition = cond) } }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) cond.labelAr else cond.labelFr,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) OnEmeraldContainer else MaterialTheme.colorScheme.onSurface
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = OnEmeraldContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Step5Location(
    formState: CreateAdFormState,
    isArabic: Boolean,
    onFormChange: ((CreateAdFormState) -> CreateAdFormState) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredWilayas = remember(searchQuery) {
        if (searchQuery.isBlank()) WilayasData.allWilayas
        else WilayasData.allWilayas.filter {
            it.code.contains(searchQuery) ||
                    it.nameAr.contains(searchQuery, ignoreCase = true) ||
                    it.nameFr.contains(searchQuery, ignoreCase = true)
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isArabic) "مكان تواجد السلعة (اختر من 69 ولاية)" else "Localisation (69 Wilayas)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (isArabic) "ابحث عن الولاية بالاسم أو الرقم..." else "Filtrer la wilaya...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                items(filteredWilayas) { w ->
                    val isSelected = formState.wilayaCode == w.code
                    Surface(
                        color = if (isSelected) EmeraldContainer else Color.Transparent,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onFormChange {
                                    it.copy(
                                        wilayaCode = w.code,
                                        commune = w.communes.firstOrNull() ?: ""
                                    )
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = w.code,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.width(30.dp)
                            )
                            Text(
                                text = if (isArabic) "${w.nameAr} (${w.nameFr})" else "${w.nameFr} (${w.nameAr})",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) OnEmeraldContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = formState.commune,
                onValueChange = { c -> onFormChange { it.copy(commune = c) } },
                label = { Text(if (isArabic) "البلدية أو الحي" else "Commune / Quartier") },
                placeholder = { Text(if (isArabic) "مثال: بئر مراد رايس" else "Ex: Bir Mourad Raïs") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
fun Step6Photos(formState: CreateAdFormState, isArabic: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isArabic) "صور المنتج (تزيد فرصة البيع بـ 4 أضعاف)" else "Photos de l'article",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isArabic) "تم تحديد 3 صور للمعرض التجريبي" else "3 photos d'illustration ajoutées",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun Step7Contact(
    formState: CreateAdFormState,
    isArabic: Boolean,
    onFormChange: ((CreateAdFormState) -> CreateAdFormState) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isArabic) "بيانات الاتصال والتواصل" else "Vos coordonnées",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.sellerPhone,
                onValueChange = { phone -> onFormChange { it.copy(sellerPhone = phone) } },
                label = { Text(if (isArabic) "رقم الهاتف للاتصال والواتساب" else "Numéro de téléphone") },
                placeholder = { Text("0661234567") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
fun Step8Delivery(formState: CreateAdFormState, isArabic: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isArabic) "خيارات التوصيل والاستلام" else "Options de livraison",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            listOf(
                "توصيل متوفر لـ 69 ولاية (Yalidine Express / Maystro)",
                "استلام يد بيد في مكان المعاينة (Main propre)",
                "توصيل محلي في حدود الولاية فقط"
            ).forEachIndexed { idx, option ->
                Surface(
                    color = if (idx == 0) EmeraldContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = if (idx == 0) OnEmeraldContainer else SlateMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            fontWeight = if (idx == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (idx == 0) OnEmeraldContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Step9Review(formState: CreateAdFormState, isArabic: Boolean) {
    val wilaya = WilayasData.findWilayaByCode(formState.wilayaCode)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isArabic) "مراجعة معلومات الإعلان قبل الدفع" else "Récapitulatif de l'annonce",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formState.title.ifBlank { "إعلان بدون عنوان" },
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = EmeraldPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatPriceDzd(formState.priceText.toLongOrNull() ?: 0L, isArabic),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = AmberAccent
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${formState.commune} • ${wilaya?.displayName ?: formState.wilayaCode}",
                fontSize = 12.sp,
                color = SlateMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formState.description.ifBlank { "لا يوجد وصف" },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = AmberContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = OnAmberContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "المرحلة التالية: دفع رسوم النشر 200 دج وإدخال رقم العملية لنشر إعلانك."
                        else "Étape suivante : Paiement des frais de 200 DZD pour validation.",
                        fontSize = 11.sp,
                        color = OnAmberContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun Step10Payment200Dzd(
    formState: CreateAdFormState,
    platformSettings: PlatformSettings,
    isArabic: Boolean,
    context: Context,
    onFormChange: ((CreateAdFormState) -> CreateAdFormState) -> Unit,
    onSubmit: () -> Unit,
    onViewMyAds: () -> Unit
) {
    if (formState.submittedAdId != null) {
        // Success state
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StatusGreen,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isArabic) "تم إرسال إعلانك وإثبات دفع 200 دج بنجاح!" else "Annonce & Reçu 200 DZD transmis !",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = StatusGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isArabic) "يقوم المشرف حاليًا بمراجعة رقم الحوالة ${formState.paymentReference}. سيتم تفعيل ونشر الإعلان فور التحقق."
                    else "Votre reçu ${formState.paymentReference} est en cours de validation.",
                    fontSize = 12.sp,
                    color = SlateMuted
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onViewMyAds,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isArabic) "الانتقال إلى إعلاناتي" else "Voir mes annonces",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        return
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isArabic) "رسوم نشر الإعلان: 200 دج ثابتة" else "Frais de publication : 200 DZD",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldPrimary
                    )
                    Text(
                        text = if (isArabic) "«لا إعلان منشور بدون دفع رسوم النشر 200 دج والتحقق منها»"
                        else "Aucune annonce publiée sans vérification du paiement 200 DZD",
                        fontSize = 11.sp,
                        color = SlateMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Official CCP / BaridiMob Box
            Surface(
                color = EmeraldContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isArabic) "معلومات الدفع لبريد الجزائر (CCP / BaridiMob):" else "Coordonnées de paiement (CCP / BaridiMob) :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = OnEmeraldContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // CCP Account
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "حساب بريد الجزائر CCP:",
                                fontSize = 11.sp,
                                color = OnEmeraldContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${platformSettings.ccpAccount} Clé ${platformSettings.ccpKey}",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = OnEmeraldContainer
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("CCP", "${platformSettings.ccpAccount} ${platformSettings.ccpKey}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ رقم الـ CCP", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy CCP",
                                tint = OnEmeraldContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // BaridiMob RIP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "رقم الحساب البريدي بريدي موب (RIP BaridiMob):",
                                fontSize = 11.sp,
                                color = OnEmeraldContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = platformSettings.baridiMobRip,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = OnEmeraldContainer
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("RIP", platformSettings.baridiMobRip)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ رقم الـ RIP", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy RIP",
                                tint = OnEmeraldContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Security Notice (Anti-Phishing Guarantee)
            Surface(
                color = AmberContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = OnAmberContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "تنبيه أمان: تطبيق AchriDZ لا يطلب أبدًا كلمة المرور الخاصة ببريدي موب أو الرمز السري للبطاقة الذهبية."
                        else "Sécurité : AchriDZ ne demande JAMAIS votre mot de passe BaridiMob ou code Edahabia.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnAmberContainer,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Proof form: Transaction Reference Input
            OutlinedTextField(
                value = formState.paymentReference,
                onValueChange = { ref -> onFormChange { it.copy(paymentReference = ref) } },
                label = { Text(if (isArabic) "رقم العملية أو الحوالة (Référence de transaction)" else "Référence de transaction CCP/BaridiMob") },
                placeholder = { Text("Ex: BM-2026-981240") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("payment_reference_input"),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Receipt image upload placeholder
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (formState.paymentReference.isBlank()) {
                            onFormChange { it.copy(paymentReference = "BM-2026-${(10000..99999).random()}") }
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isArabic) "صورة وصل التحويل (Reçu de paiement)" else "Photo du reçu de paiement",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) "انقر لتوليد أو إرفاق وصل التحويل التجريبي 200 دج" else "Joindre le reçu de 200 DZD",
                            fontSize = 10.sp,
                            color = SlateMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Final Submit Button
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_ad_and_payment_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isArabic) "تأكيد إرسال الإعلان ووصل الدفع 200 دج 🚀" else "Soumettre l'annonce et le reçu 200 DZD 🚀",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
