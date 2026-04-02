package com.washwise.mobile

import android.app.Application
import com.washwise.mobile.utils.SharedPrefManager

class WashWiseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPrefManager.init(this)
    }
}