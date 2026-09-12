package com.example.admin

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp

class AdminApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        val firebaseApp = FirebaseApp.initializeApp(this)
        if (firebaseApp == null) {
            Log.e(TAG, "Firebase is not configured. Add google-services.json to the admin app.")
        } else {
            Log.d(TAG, "Firebase initialized for project ${firebaseApp.options.projectId}")
        }
        AdminNotificationHelper.createChannel(applicationContext)
    }
    companion object {
        private const val TAG = "AdminApplication"
        lateinit var context: Context
    }
}
