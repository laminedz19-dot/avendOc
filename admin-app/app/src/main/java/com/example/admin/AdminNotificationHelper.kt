package com.example.admin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object AdminNotificationHelper {
    private const val CHANNEL_ID = "pending_listings"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "مراجعة الإعلانات", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "إشعارات الإعلانات الجديدة التي تحتاج موافقة الأدمين"
            }
        )
    }

    fun notifyNewListing(context: Context, title: String) {
        showRemoteNotification(context, "إعلان جديد يحتاج المراجعة", title, null)
    }

    fun showRemoteNotification(context: Context, title: String, body: String, listingId: String?) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify((listingId ?: body).hashCode(), notification)
    }
}
