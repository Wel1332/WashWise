package com.washwise.mobile.data.model.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val fullName: String
)