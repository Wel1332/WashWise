package com.washwise.mobile.feature.order.data

data class CreateOrderRequest(
    val serviceId: String,
    val totalPrice: Double,
    val location: String,
    val scheduledDate: String,
    val notes: String?
)
