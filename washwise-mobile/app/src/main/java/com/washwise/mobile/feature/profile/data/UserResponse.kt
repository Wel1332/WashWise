package com.washwise.mobile.feature.profile.data

data class UserResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String,
    val bio: String?,
    val phoneNumber: String?,
    val address: String?,
    val city: String?,
    val zipCode: String?,
    val profileImageUrl: String?,
    val createdAt: String,
    val updatedAt: String
)