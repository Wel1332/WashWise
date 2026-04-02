package com.washwise.mobile.data.model.request

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)