package com.example.admin

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.security.MessageDigest

class AdminMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        registerToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.data["title"] ?: message.notification?.title ?: "إعلان جديد يحتاج المراجعة"
        val body = message.data["body"] ?: message.notification?.body ?: "تمت إضافة إعلان جديد"
        AdminNotificationHelper.showRemoteNotification(this, title, body, message.data["listingId"])
    }

    private fun registerToken(token: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        FirebaseFirestore.getInstance().collection("adminTokens").document(token.hashForDocumentId()).set(
            mapOf(
                "token" to token,
                "adminUid" to user.uid,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        )
    }
}

fun registerCurrentAdminMessagingToken() {
    val user = FirebaseAuth.getInstance().currentUser ?: return
    com.google.firebase.messaging.FirebaseMessaging.getInstance().token
        .addOnSuccessListener { token ->
            FirebaseFirestore.getInstance().collection("adminTokens").document(token.hashForDocumentId()).set(
                mapOf(
                    "token" to token,
                    "adminUid" to user.uid,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
        }
}

private fun String.hashForDocumentId(): String = MessageDigest.getInstance("SHA-256")
    .digest(toByteArray())
    .joinToString("") { "%02x".format(it) }
