package com.washwise.mobile.feature.profile.data

data class UpdateProfileRequest(
    val fullName: String?,
    val bio: String?,
    val phoneNumber: String?,
    val address: String?,
    val city: String?,
    val zipCode: String?
)