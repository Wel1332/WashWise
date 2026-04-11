package com.washwise.mobile.feature.auth.data

data class RegisterRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val fullName: String
)