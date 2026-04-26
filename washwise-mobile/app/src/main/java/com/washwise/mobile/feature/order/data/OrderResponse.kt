package com.washwise.mobile.feature.order.data

data class OrderResponse(
    val id: String,
    val totalPrice: Double? = null,
    val location: String? = null,
    val scheduledDate: String? = null,
    val completedDate: String? = null,
    val notes: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val weightKg: Double? = null,
    val service: ServiceInfo? = null,
    val user: UserInfo? = null
) {
    data class ServiceInfo(
        val id: String? = null,
        val name: String? = null,
        val category: String? = null,
        val description: String? = null,
        val price: Double? = null,
        val duration: String? = null,
        val imageUrl: String? = null
    )

    data class UserInfo(
        val id: String? = null,
        val fullName: String? = null,
        val email: String? = null
    )
}
