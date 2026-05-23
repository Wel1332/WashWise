package com.washwise.mobile.feature.admin.data
data class AdminUser(
    val id: String,
    val email: String? = null,
    val fullName: String? = null,
    val role: String? = null,
    val createdAt: String? = null
)
