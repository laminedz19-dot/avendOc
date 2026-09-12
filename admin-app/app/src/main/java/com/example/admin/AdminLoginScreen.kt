package com.example.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminLoginScreen(onSignedIn: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text("دخول الأدمين", fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("سجّل الدخول لإدارة إعلانات avendOc", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(28.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("البريد الإلكتروني") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("كلمة المرور") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "إخفاء" else "إظهار", fontSize = 11.sp)
                    }
                }
            )
            Spacer(Modifier.height(12.dp))

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "أدخل البريد الإلكتروني وكلمة المرور."
                        return@Button
                    }
                    if (FirebaseApp.getApps(AdminApplication.context).isEmpty()) {
                        errorMessage = "Firebase غير مهيأ. أضف google-services.json قبل تسجيل الدخول."
                        return@Button
                    }
                    isLoading = true
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password)
                        .addOnSuccessListener { result ->
                            val signedInUser = result.user
                            if (signedInUser == null) {
                                isLoading = false
                                errorMessage = "تعذر إنشاء جلسة آمنة. حاول مرة أخرى."
                            } else {
                                signedInUser.getIdToken(true)
                                .addOnSuccessListener { tokenResult ->
                                    isLoading = false
                                    if (tokenResult.claims["admin"] == true) {
                                        registerCurrentAdminMessagingToken()
                                        onSignedIn()
                                    } else {
                                        FirebaseAuth.getInstance().signOut()
                                        errorMessage = "هذا الحساب لا يملك صلاحيات الأدمين."
                                    }
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                    FirebaseAuth.getInstance().signOut()
                                    errorMessage = "تعذر التحقق من صلاحيات الأدمين."
                                }
                            }
                        }
                        .addOnFailureListener {
                            isLoading = false
                            errorMessage = authErrorMessage(it)
                        }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.padding(2.dp))
                else Text("تسجيل الدخول")
            }
            Spacer(Modifier.height(18.dp))
            Text("يجب أن يكون الحساب مفعّلًا بصلاحية admin في Firebase.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun authErrorMessage(error: Exception): String = when {
    error.message?.contains("password", ignoreCase = true) == true || error.message?.contains("credential", ignoreCase = true) == true -> "البريد الإلكتروني أو كلمة المرور غير صحيحة."
    error.message?.contains("network", ignoreCase = true) == true -> "تعذر الاتصال بالإنترنت. حاول مرة أخرى."
    else -> "تعذر تسجيل الدخول. تحقق من بياناتك وإعدادات Firebase."
}

fun signOutAdmin() = FirebaseAuth.getInstance().signOut()
