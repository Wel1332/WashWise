package com.washwise.mobile.shared.util

import android.content.Context
import android.content.SharedPreferences

object SharedPrefManager {

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    }

    // This is the combined function your LoginActivity is looking for!
    fun saveAuthSession(token: String, refreshToken: String, id: String, name: String, email: String, role: String) {
        prefs.edit().apply {
            putString(Constants.KEY_TOKEN, token)
            putString(Constants.KEY_REFRESH_TOKEN, refreshToken)
            putString(Constants.KEY_USER_ID, id)
            putString(Constants.KEY_USER_NAME, name)
            putString(Constants.KEY_USER_EMAIL, email)
            putString(Constants.KEY_USER_ROLE, role)
            putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(Constants.KEY_TOKEN, null)

    fun isLoggedIn(): Boolean = prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)

    fun getUserName(): String? = prefs.getString(Constants.KEY_USER_NAME, null)

    fun getUserEmail(): String? = prefs.getString(Constants.KEY_USER_EMAIL, null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}