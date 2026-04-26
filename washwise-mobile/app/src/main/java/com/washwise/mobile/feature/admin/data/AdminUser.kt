package com.washwise.mobile.feature.admin.data

/**
 * User row returned by `GET /api/v1/users`. Mirrors the backend's UserResponse
 * with optional fields nullable so Gson never blows up on a missing field.
 */
data class AdminUser(
    val id: String,
    val email: String? = null,
    val fullName: String? = null,
    val role: String? = null,
    val createdAt: String? = null
)
