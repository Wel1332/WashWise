package com.washwise.mobile.utils

object Constants {
    // TODO: Replace with your actual backend URL
    const val BASE_URL = "http://10.0.2.2:8080/api/v1/"  // Android emulator localhost
    // For real device use: const val BASE_URL = "http://YOUR_IP:8080/api/v1/"

    // SharedPreferences keys
    const val PREF_NAME = "WashWisePrefs"
    const val KEY_TOKEN = "token"
    const val KEY_REFRESH_TOKEN = "refreshToken"
    const val KEY_USER_ID = "userId"
    const val KEY_USER_NAME = "userName"
    const val KEY_USER_EMAIL = "userEmail"
    const val KEY_USER_ROLE = "userRole"
    const val KEY_IS_LOGGED_IN = "isLoggedIn"
}