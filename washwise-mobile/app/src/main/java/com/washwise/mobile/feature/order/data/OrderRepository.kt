package com.washwise.mobile.feature.order.data

import com.washwise.mobile.shared.api.ApiService
import com.washwise.mobile.shared.api.RetrofitClient

/**
 * Data boundary for the order slice.
 */
class OrderRepository(
    private val api: ApiService = RetrofitClient.instance
) {

    suspend fun getMyOrders(): Result<List<OrderResponse>> = runCatching {
        val response = api.getMyOrders()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to load orders (HTTP ${response.code()})")
        }
        body.data.orEmpty()
    }

    suspend fun createOrder(request: CreateOrderRequest): Result<OrderResponse> = runCatching {
        val response = api.createOrder(request)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to create order (HTTP ${response.code()})")
        }
        body.data ?: error("Create order response missing data")
    }

    suspend fun cancelOrder(id: String): Result<Unit> = runCatching {
        val response = api.cancelOrder(id)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to cancel order (HTTP ${response.code()})")
        }
    }
}
