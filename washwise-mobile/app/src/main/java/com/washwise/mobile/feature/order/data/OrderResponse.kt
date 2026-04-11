package com.washwise.mobile.feature.order.data

data class OrderResponse(
    val id: String,
    val totalPrice: Double,
    val location: String?,
    val scheduledDate: String?,
    val completedDate: String?,
    val notes: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val service: ServiceInfo?
) {
    data class ServiceInfo(
        val id: String,
        val name: String,
        val category: String?,
        val price: Double
    )
}
