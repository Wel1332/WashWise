package com.washwise.mobile.feature.admin.data
data class CreateServiceRequest(
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val duration: String,
    val isActive: Boolean = true
)
