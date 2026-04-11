package com.washwise.mobile.feature.profile.data

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)