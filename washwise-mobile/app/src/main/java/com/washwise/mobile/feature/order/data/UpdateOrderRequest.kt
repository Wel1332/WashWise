package com.washwise.mobile.feature.order.data

/**
 * Body for `PUT /orders/{id}`. All fields are optional — only those present
 * are applied by the backend (see UpdateOrderRequest.java in washwise-backend).
 */
data class UpdateOrderRequest(
    val status: String? = null,
    val notes: String? = null,
    val location: String? = null
)
