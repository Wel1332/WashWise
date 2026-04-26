package com.washwise.mobile.feature.admin.data

/**
 * Body for `POST /services` (admin only). Mirrors the backend's CreateServiceRequest:
 * name (3-100), description (10-500), price > 0, category (3-50), duration (<=50).
 */
data class CreateServiceRequest(
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val duration: String,
    val isActive: Boolean = true
)
