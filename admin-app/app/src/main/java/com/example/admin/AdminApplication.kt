package com.example.admin

import android.app.Application
import android.content.Context

class AdminApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
    companion object { lateinit var context: Context }
}
