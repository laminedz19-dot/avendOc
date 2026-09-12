package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppLanguage
import com.example.data.UserAccount
import com.example.data.UserRole
import com.example.data.WilayasData
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SlateMuted

@Composable
fun AuthDialog(
    userAccount: UserAccount,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onLogin: (phone: String, pass: String, role: UserRole) -> Unit,
    onRegister: (name: String, phone: String, wilayaCode: String, role: UserRole) -> Unit
) {
    val isArabic = currentLanguage == AppLanguage.ARABIC

    // Registration Form State
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regWilayaCode by remember { mutableStateOf("16") }
    var regPassword by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf(UserRole.SELLER) }
    var isRegisterExpanded by remember { mutableStateOf(true) }

    // Login Form State
    var loginPhone by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginRole by remember { mutableStateOf(UserRole.SELLER) }
    var isLoginExpanded by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("auth_dialog_surface"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with App Identity & Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AchriDZ",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = EmeraldPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "🇩🇿", fontSize = 16.sp)
                            }
                            Text(
                                text = if (isArabic) "سوق الجزائر عبر 69 ولاية" else "Marketplace 69 Wilayas",
                                fontSize = 11.sp,
                                color = SlateMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("auth_dialog_close")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Welcome banner
                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isArabic)
                            "👋 أهلاً بك! يرجى التسجيل أو تسجيل الدخول لإدارة إعلاناتك ومراسلة البائعين والمشترين."
                        else
                            "👋 Bienvenue ! Inscrivez-vous ou connectez-vous pour publier et discuter.",
                        fontSize = 12.sp,
                        color = OnEmeraldContainer,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // -------------------------------------------------------------
                // 1. القائمة: التسجيل (إنشاء حساب جديد)
                // -------------------------------------------------------------
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .testTag("section_register")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isRegisterExpanded = !isRegisterExpanded },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "1. التسجيل (حساب جديد)" else "1. Inscription (Nouveau compte)",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = EmeraldPrimary
                                    )
                                    Text(
                                        text = if (isArabic) "أنشئ حسابك للبدء في نشر الإعلانات" else "Créez votre compte en quelques secondes",
                                        fontSize = 11.sp,
                                        color = SlateMuted
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isRegisterExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = EmeraldPrimary
                            )
                        }

                        AnimatedVisibility(visible = isRegisterExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                OutlinedTextField(
                                    value = regName,
                                    onValueChange = { regName = it },
                                    label = { Text(if (isArabic) "الاسم واللقب" else "Nom complet") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_name_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = regPhone,
                                    onValueChange = { regPhone = it },
                                    label = { Text(if (isArabic) "رقم الهاتف (05/06/07)" else "Numéro de téléphone") },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldPrimary) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_phone_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = regPassword,
                                    onValueChange = { regPassword = it },
                                    label = { Text(if (isArabic) "كلمة المرور" else "Mot de passe") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_pass_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        onRegister(
                                            regName.ifBlank { "مستخدم جديد" },
                                            regPhone.ifBlank { "0661234567" },
                                            regWilayaCode,
                                            regRole
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("submit_registration_button")
                                ) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "التسجيل وإنشاء الحساب" else "S'inscrire maintenant",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // -------------------------------------------------------------
                // 2. وتحتها مباشرة: الدخول (تسجيل الدخول)
                // -------------------------------------------------------------
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .testTag("section_login")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isLoginExpanded = !isLoginExpanded },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(AmberAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "2. الدخول (تسجيل الدخول)" else "2. Connexion (Se connecter)",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = AmberAccent
                                    )
                                    Text(
                                        text = if (isArabic) "للمستخدمين المسجلين سابقاً" else "Pour les utilisateurs déjà enregistrés",
                                        fontSize = 11.sp,
                                        color = SlateMuted
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isLoginExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = AmberAccent
                            )
                        }

                        AnimatedVisibility(visible = isLoginExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                OutlinedTextField(
                                    value = loginPhone,
                                    onValueChange = { loginPhone = it },
                                    label = { Text(if (isArabic) "رقم الهاتف أو اسم المستخدم" else "Téléphone ou identifiant") },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AmberAccent) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_phone_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = loginPassword,
                                    onValueChange = { loginPassword = it },
                                    label = { Text(if (isArabic) "كلمة المرور" else "Mot de passe") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AmberAccent) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_pass_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        onLogin(
                                            loginPhone.ifBlank { "0661234567" },
                                            loginPassword.ifBlank { "123456" },
                                            loginRole
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("submit_login_button")
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "الدخول إلى حسابي" else "Connexion",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Fast One-Tap Demo Login
                Text(
                    text = if (isArabic) "⚡ الدخول السريع بنقرة واحدة (للاختبار الفوري):" else "⚡ Connexion rapide démo :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { onLogin("0661234567", "demo", UserRole.SELLER) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "دخول سريع بنقرة واحدة (حساب تجريبي)" else "Connexion rapide démo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isArabic) "تصفح الإعلانات كزائر الآن ✕" else "Continuer en mode invité ✕",
                        color = SlateMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
