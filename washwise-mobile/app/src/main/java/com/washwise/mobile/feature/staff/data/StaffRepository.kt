package com.washwise.mobile.feature.staff.data

import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.feature.order.data.UpdateOrderRequest
import com.washwise.mobile.shared.api.ApiService
import com.washwise.mobile.shared.api.RetrofitClient

/**
 * Data boundary for the Staff slice. Exposes the operations a staff user needs
 * (view the global queue and advance an order's status) while keeping the
 * presenter free of Retrofit details.
 */
class StaffRepository(
    private val api: ApiService = RetrofitClient.instance
) {

    suspend fun getAllOrders(): Result<List<OrderResponse>> = runCatching {
        val response = api.getAllOrders()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to load orders (HTTP ${response.code()})")
        }
        body.data.orEmpty()
    }

    suspend fun updateStatus(orderId: String, newStatus: String): Result<OrderResponse> = runCatching {
        val response = api.updateOrder(orderId, UpdateOrderRequest(status = newStatus))
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to update status (HTTP ${response.code()})")
        }
        body.data ?: error("Update response missing data")
    }
}
