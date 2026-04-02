package com.washwise.mobile.data.model.request

data class UpdateProfileRequest(
    val fullName: String?,
    val bio: String?,
    val phoneNumber: String?,
    val address: String?,
    val city: String?,
    val zipCode: String?
)