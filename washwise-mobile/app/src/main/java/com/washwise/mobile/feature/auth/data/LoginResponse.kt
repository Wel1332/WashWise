package com.washwise.mobile.feature.auth.data

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: UserData
)

data class UserData(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String
)