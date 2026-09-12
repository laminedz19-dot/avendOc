package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AppLanguage
import com.example.data.CategoryType
import com.example.data.DeliveryOption
import com.example.data.ItemCondition
import com.example.data.PlatformSettings
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
import com.example.ui.theme.SlateMuted
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
    onViewMyAds: () -> Unit,
    onUploadReceipt: (String?) -> Unit,
    onRemoveReceipt: () -> Unit
) {
    val isArabic = currentLanguage == AppLanguage.ARABIC
    val context = LocalContext.current
    var validationError by remember { mutableStateOf<String?>(null) }

    val stepTitlesAr = listOf(
        "1. اختيار الفئة",
        "2. العنوان والوصف (إجباري)",
        "3. السعر والتفاوض (إجباري)",
        "4. حالة المنتج",
        "5. الولاية والبلدية (إجباري)",
        "6. رفع صور المنتج (إجباري)",
        "7. بيانات الاتصال (إجباري)",
        "8. اختيار طريقة التوصيل (إجباري)",
        "9. مراجعة معلومات الإعلان",
        "10. دفع الرسوم وتأكيد النشر"
    )

    val stepTitlesFr = listOf(
        "1. Catégorie",
        "2. Titre & Description (Obligatoire)",
        "3. Prix & Négociation (Obligatoire)",
        "4. État de l'article",
        "5. Wilaya & Commune (Obligatoire)",
        "6. Photos du produit (Obligatoire)",
        "7. Contact (Obligatoire)",
        "8. Mode de livraison (Obligatoire)",
        "9. Récapitulatif de l'annonce",
        "10. Paiement & Confirmation"
    )

    val currentTitle = if (isArabic) stepTitlesAr[formState.currentStep - 1] else stepTitlesFr[formState.currentStep - 1]

    // Mandatory validation per step
    fun validateCurrentStep(): Boolean {
        when (formState.currentStep) {
            2 -> {
                if (formState.title.trim().length < 4) {
                    validationError = if (isArabic) "يرجى كتابة عنوان واضح للإعلان (على الأقل 4 أحرف)!" else "Le titre doit comporter au moins 4 caractères !"
                    return false
                }
                if (formState.description.trim().length < 10) {
                    validationError = if (isArabic) "يرجى كتابة وصف دقيق للمنتج (على الأقل 10 أحرف)!" else "La description doit comporter au moins 10 caractères !"
                    return false
                }
            }
            3 -> {
                val price = formState.priceText.toLongOrNull()
                if (price == null || price <= 0) {
                    validationError = if (isArabic) "يرجى إدخال سعر صحيح للمنتج بالدينار الجزائري!" else "Veuillez entrer un prix valide en DZD !"
                    return false
                }
            }
            5 -> {
                if (formState.wilayaCode.isBlank()) {
                    validationError = if (isArabic) "يرجى اختيار الولاية من بين 69 ولاية!" else "Veuillez sélectionner votre wilaya !"
                    return false
                }
                if (formState.commune.trim().isBlank()) {
                    validationError = if (isArabic) "يرجى إدخال البلدية أو الحي (إجباري)!" else "Veuillez indiquer la commune ou le quartier !"
                    return false
                }
            }
            6 -> {
                if (formState.selectedImages.isEmpty()) {
                    validationError = if (isArabic) "إجبارية رفع صورة واحدة على الأقل للمنتج!" else "Veuillez ajouter au moins une photo du produit !"
                    return false
                }
            }
            7 -> {
                val phone = formState.sellerPhone.trim()
                if (phone.length < 9) {
                    validationError = if (isArabic) "يرجى إدخال رقم هاتف صحيح للتواصل!" else "Veuillez entrer un numéro de téléphone valide !"
                    return false
                }
            }
        }
        validationError = null
        return true
    }

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

        // Validation Error Banner
        AnimatedVisibility(visible = validationError != null) {
            Surface(
                color = Color(0xFFFEE2E2),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = validationError ?: "",
                        color = Color(0xFF991B1B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { validationError = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF991B1B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Multi-Step Content Body with Animated Transitions
        AnimatedContent(
            targetState = formState.currentStep,
            transitionSpec = {
                val forward = targetState > initialState
                val multiplier = if (isArabic) -1 else 1
                val direction = if (forward) multiplier else -multiplier
                (slideInHorizontally(
                    initialOffsetX = { (it * 0.2f * direction).toInt() },
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                ) + fadeIn(animationSpec = tween(220))).togetherWith(
                    slideOutHorizontally(
                        targetOffsetX = { (-it * 0.2f * direction).toInt() },
                        animationSpec = tween(180)
                    ) + fadeOut(animationSpec = tween(180))
                )
            },
            label = "create_ad_step_anim",
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { step ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (step) {
                    1 -> item { Step1Category(formState, isArabic, onFormChange) }
                    2 -> item { Step2TitleDescription(formState, isArabic, onFormChange) }
                    3 -> item { Step3PriceNegotiation(formState, isArabic, onFormChange) }
                    4 -> item { Step4Condition(formState, isArabic, onFormChange) }
                    5 -> item { Step5Location(formState, isArabic, onFormChange) }
                    6 -> item { Step6Photos(formState, isArabic, onFormChange) }
                    7 -> item { Step7Contact(formState, isArabic, onFormChange) }
                    8 -> item { Step8Delivery(formState, isArabic, onFormChange) }
                    9 -> item {
                        Step9Review(
                            formState = formState,
                            isArabic = isArabic,
                            onNextToPayment = {
                                if (validateCurrentStep()) {
                                    onNextStep()
                                }
                            }
                        )
                    }
                    10 -> item {
                        Step10PaymentAndVerification(
                            formState = formState,
                            platformSettings = platformSettings,
                            isArabic = isArabic,
                            onUploadReceipt = onUploadReceipt,
                            onRemoveReceipt = onRemoveReceipt,
                            onSubmit = onSubmit,
                            onViewMyAds = onViewMyAds,
                            onReset = onReset
                        )
                    }
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
                        onClick = {
                            validationError = null
                            onPrevStep()
                        },
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
                        onClick = {
                            if (validateCurrentStep()) {
                                onNextStep()
                            }
                        },
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
                } else {
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.testTag("submit_ad_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "إرسال للمراجعة الإدارية" else "Envoyer pour validation",
                            fontWeight = FontWeight.Bold
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
                text = if (isArabic) "اختر فئة الإعلان *" else "Choisissez la catégorie *",
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isArabic) "عنوان الإعلان وتفاصيله" else "Titre et description",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "(إجباري)" else "(Obligatoire)",
                    fontSize = 12.sp,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.title,
                onValueChange = { title -> onFormChange { it.copy(title = title) } },
                label = { Text(if (isArabic) "عنوان الإعلان (مطلوب) *" else "Titre de l'annonce *") },
                placeholder = { Text(if (isArabic) "مثال: هاتف سامسونج S23 ألترا بحالة ممتازة" else "Ex: Samsung Galaxy S23 Ultra") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_ad_title_input"),
                shape = RoundedCornerShape(10.dp),
                isError = formState.title.isNotBlank() && formState.title.trim().length < 4
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.description,
                onValueChange = { desc -> onFormChange { it.copy(description = desc) } },
                label = { Text(if (isArabic) "الوصف الدقيق للمنتج (مطلوب) *" else "Description détaillée *") },
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
                shape = RoundedCornerShape(10.dp),
                isError = formState.description.isNotBlank() && formState.description.trim().length < 10
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isArabic) "السعر وخاصية المساومة (التفاوض)" else "Prix et négociabilité",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "(إجباري)" else "(Obligatoire)",
                    fontSize = 12.sp,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.priceText,
                onValueChange = { p -> onFormChange { it.copy(priceText = p.filter { ch -> ch.isDigit() }) } },
                label = { Text(if (isArabic) "السعر المطلوب بالدينار الجزائري (دج) *" else "Prix en DZD *") },
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
                text = if (isArabic) "ما هي حالة المنتج؟ *" else "Quel est l'état de l'article ? *",
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isArabic) "مكان تواجد السلعة (اختر من 69 ولاية)" else "Localisation (69 Wilayas)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "(إجباري)" else "(Obligatoire)",
                    fontSize = 12.sp,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Bold
                )
            }
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
                label = { Text(if (isArabic) "البلدية أو الحي (مطلوب) *" else "Commune / Quartier *") },
                placeholder = { Text(if (isArabic) "مثال: بئر مراد رايس" else "Ex: Bir Mourad Raïs") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
fun Step6Photos(
    formState: CreateAdFormState,
    isArabic: Boolean,
    onFormChange: ((CreateAdFormState) -> CreateAdFormState) -> Unit
) {
    // Multiple photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 6)
    ) { uris ->
        if (uris.isNotEmpty()) {
            onFormChange { state ->
                val current = state.selectedImages.toMutableList()
                uris.forEach { u ->
                    val uriStr = u.toString()
                    if (!current.contains(uriStr)) {
                        current.add(uriStr)
                    }
                }
                state.copy(selectedImages = current)
            }
        }
    }

    // Default sample pictures for demo quick-add
    val sampleCarPhotos = listOf(
        "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=600",
        "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=600",
        "https://images.unsplash.com/photo-1511919884226-fd3cad34687c?w=600"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) "صور المنتج" else "Photos du produit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "(إجباري - صورة على الأقل)" else "(Obligatoire)",
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = if (formState.selectedImages.isNotEmpty()) EmeraldContainer else AmberContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${formState.selectedImages.size} / 6",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (formState.selectedImages.isNotEmpty()) OnEmeraldContainer else OnAmberContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons to Add Photos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("pick_product_photos_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "رفع من المعرض" else "Choisir photos",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        onFormChange { state ->
                            val current = state.selectedImages.toMutableList()
                            sampleCarPhotos.forEach { url ->
                                if (!current.contains(url)) current.add(url)
                            }
                            state.copy(selectedImages = current)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isArabic) "+ صور نموذجية" else "+ Échantillons",
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Display selected photos or empty state
            if (formState.selectedImages.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = SlateMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isArabic) "لم يتم اختيار أي صورة بعد (مطلوبة للمتابعة)" else "Aucune photo sélectionnée",
                            fontSize = 12.sp,
                            color = SlateMuted
                        )
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(formState.selectedImages) { photoUrl ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, EmeraldPrimary, RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Product Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Remove photo button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .clickable {
                                        onFormChange { state ->
                                            state.copy(selectedImages = state.selectedImages.filterNot { it == photoUrl })
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isArabic) "بيانات الاتصال والتواصل" else "Vos coordonnées",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "(إجباري)" else "(Obligatoire)",
                    fontSize = 12.sp,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.sellerPhone,
                onValueChange = { phone -> onFormChange { it.copy(sellerPhone = phone) } },
                label = { Text(if (isArabic) "رقم الهاتف للاتصال والواتساب (مطلوب) *" else "Numéro de téléphone *") },
                placeholder = { Text("0661234567") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
fun Step8Delivery(
    formState: CreateAdFormState,
    isArabic: Boolean,
    onFormChange: ((CreateAdFormState) -> CreateAdFormState) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isArabic) "اختيار طريقة التوصيل المتاحة للمشتري" else "Mode de livraison proposé",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "(إجباري)" else "(Obligatoire)",
                    fontSize = 12.sp,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isArabic) "حدد الكيفية التي يمكن للمشتري استلام السلعة بها:"
                else "Indiquez comment l'acheteur pourra récupérer l'article :",
                fontSize = 12.sp,
                color = SlateMuted
            )
            Spacer(modifier = Modifier.height(12.dp))

            DeliveryOption.values().forEach { option ->
                val isSelected = formState.deliveryOption == option
                Surface(
                    color = if (isSelected) EmeraldContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onFormChange { it.copy(deliveryOption = option) }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else SlateMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) option.labelAr else option.labelFr,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) OnEmeraldContainer else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isArabic) option.subtitleAr else option.subtitleFr,
                                fontSize = 11.sp,
                                color = if (isSelected) OnEmeraldContainer.copy(alpha = 0.8f) else SlateMuted
                            )
                        }
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Step9Review(
    formState: CreateAdFormState,
    isArabic: Boolean,
    onNextToPayment: () -> Unit
) {
    val wilaya = WilayasData.findWilayaByCode(formState.wilayaCode)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isArabic) "مراجعة معلومات الإعلان قبل النشر" else "Récapitulatif de l'annonce",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formState.title.ifBlank { "إعلان بدون عنوان" },
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = EmeraldPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatPriceDzd(formState.priceText.toLongOrNull() ?: 0L, isArabic),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = AmberAccent
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${formState.commune} • ${wilaya?.displayName ?: formState.wilayaCode}",
                fontSize = 13.sp,
                color = SlateMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Display delivery option selected
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
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) formState.deliveryOption.labelAr else formState.deliveryOption.labelFr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnEmeraldContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formState.description.ifBlank { "لا يوجد وصف" },
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            // Photos Preview in Review
            if (formState.selectedImages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(formState.selectedImages) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Review photo",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider()
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = AmberContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = OnAmberContainer
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isArabic) "الخطوة التالية (10): تفاصيل دفع رسوم النشر (300 دج) وإمكانية إرفاق صورة الوصل لإرسال الإعلان إلى مراجعة الإدارة."
                        else "Étape suivante (10) : Frais de publication 300 DZD et téléversement du reçu.",
                        fontSize = 12.sp,
                        color = OnAmberContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNextToPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("go_to_payment_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isArabic) "المتابعة للخطوة النهائية (10) ←" else "Procéder à l'étape finale (10) ←",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun Step10PaymentAndVerification(
    formState: CreateAdFormState,
    platformSettings: PlatformSettings,
    isArabic: Boolean,
    onUploadReceipt: (String?) -> Unit,
    onRemoveReceipt: () -> Unit,
    onSubmit: () -> Unit,
    onViewMyAds: () -> Unit,
    onReset: () -> Unit
) {
    val context = LocalContext.current

    // Photo picker for receipt
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onUploadReceipt(uri.toString())
        }
    }

    if (formState.submittedAdId != null) {
        // Success state after direct submission
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(StatusGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusGreen,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (isArabic) "🎉 تم نشر إعلانك بنجاح في السوق!" else "🎉 Annonce publiée avec succès !",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = StatusGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isArabic) "تم حفظ جميع بيانات الإعلان وخيار التوصيل والصور المرفقة. إعلانك الآن معروض مباشرة لجميع المشترين في 69 ولاية!"
                    else "Toutes les informations, photos et mode de livraison sont enregistrés. L'annonce est active sur les 69 wilayas.",
                    fontSize = 12.sp,
                    color = SlateMuted
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                    OutlinedButton(
                        onClick = onReset,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isArabic) "إضافة إعلان جديد" else "Nouvelle annonce",
                            fontWeight = FontWeight.Medium
                        )
                    }
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
            // Title & Notice
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
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isArabic) "رسوم نشر الإعلان وتأكيد النشر" else "Frais de publication : 300 DZD",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isArabic) "بيانات الحساب البريدي الجاري CCP / بريدي موب وإرفاق الوصل"
                        else "Coordonnées CCP / BaridiMob et reçu de paiement",
                        fontSize = 11.sp,
                        color = SlateMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account details box
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AmberAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "المبلغ المطلوب:" else "Montant requis :",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Surface(
                            color = AmberAccent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "300 دج (DZD)",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isArabic) "رقم الحساب البريدي الجاري (RIP BaridiMob):" else "Numéro de compte (RIP BaridiMob) :",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = platformSettings.ccpAccount,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldPrimary
                        )
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("RIP", platformSettings.ccpAccount))
                                Toast.makeText(context, if (isArabic) "تم نسخ رقم الحساب: ${platformSettings.ccpAccount}" else "Compte copié", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isArabic) "نسخ" else "Copier", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "المفتاح (Clé): ${platformSettings.ccpKey}" else "Clé : ${platformSettings.ccpKey}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Clé", platformSettings.ccpKey))
                                Toast.makeText(context, if (isArabic) "تم نسخ المفتاح: ${platformSettings.ccpKey}" else "Clé copiée", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isArabic) "نسخ المفتاح" else "Copier Clé", fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upload Receipt Section (Manual upload, no automated AI check gating)
            Text(
                text = if (isArabic) "📸 إرفاق صورة وصل التحويل (اختياري / يدوي):" else "📸 Joindre le reçu de paiement (Optionnel / Manuel) :",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("upload_receipt_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isArabic) "رفع الوصل من المعرض" else "Choisir reçu",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (formState.uploadedReceiptUri != null) {
                    OutlinedButton(
                        onClick = onRemoveReceipt,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isArabic) "حذف الوصل" else "Supprimer", color = Color(0xFFDC2626), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display Receipt Uploaded State or Friendly Guidance
            if (formState.uploadedReceiptUri != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = formState.uploadedReceiptUri,
                            contentDescription = "Receipt Image",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "تم إرفاق صورة الوصل بنجاح" else "Reçu de paiement joint avec succès",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = OnEmeraldContainer
                            )
                            Text(
                                text = if (isArabic) "رقم الحوالة المرجعي: ${formState.paymentReference}" else "Réf : ${formState.paymentReference}",
                                fontSize = 11.sp,
                                color = OnEmeraldContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = SlateMuted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "يمكنك إرفاق صورة الوصل الآن أو إرسال الإعلان للمراجعة؛ لن يظهر في السوق قبل موافقة الإدارة."
                            else "Vous pouvez joindre le reçu maintenant ou envoyer l'annonce pour validation.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit ad button: submission is queued for admin review.
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_ad_and_payment_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "إرسال الإعلان وانتظار موافقة الإدارة" else "Envoyer et attendre la validation",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
