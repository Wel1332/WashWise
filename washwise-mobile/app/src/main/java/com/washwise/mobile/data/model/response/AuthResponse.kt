package com.washwise.mobile.data.model.response

data class AuthResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)