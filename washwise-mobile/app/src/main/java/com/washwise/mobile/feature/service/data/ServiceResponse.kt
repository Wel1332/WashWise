package com.washwise.mobile.feature.service.data

data class ServiceResponse(
    val id: String,
    val name: String,
    val description: String?,
    val category: String?,
    val price: Double,
    val duration: String?,
    val imageUrl: String?,
    val isActive: Boolean?
)
