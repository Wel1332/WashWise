package com.washwise.mobile.feature.auth.data

data class AuthResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)