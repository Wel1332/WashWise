package com.washwise.mobile.feature.order.data

data class CreateOrderRequest(
    val serviceId: String,
    val totalPrice: Double,
    val pickupAddress: String,
    val pickupDate: String,
    val pickupTimeSlot: String,
    val deliveryDate: String?,
    val deliveryTimeSlot: String?,
    val weightKg: Double,
    val specialInstructions: String?,
    val status: String = "PENDING"
)
