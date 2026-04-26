package com.washwise.mobile.feature.admin.data

import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.feature.service.data.ServiceResponse
import com.washwise.mobile.shared.api.ApiService
import com.washwise.mobile.shared.api.RetrofitClient

/**
 * Data boundary for the admin slice. Wraps the user / service / order endpoints
 * the admin tabs need so presenters never touch Retrofit.
 */
class AdminRepository(
    private val api: ApiService = RetrofitClient.instance
) {

    suspend fun getAllUsers(): Result<List<AdminUser>> = runCatching {
        val response = api.getAllUsers()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to load users (HTTP ${response.code()})")
        }
        body.data.orEmpty()
    }

    suspend fun updateUserRole(userId: String, newRole: String): Result<AdminUser> = runCatching {
        val response = api.updateUserRole(userId, UpdateRoleRequest(newRole))
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to update role (HTTP ${response.code()})")
        }
        body.data ?: error("Update response missing data")
    }

    suspend fun getAllServices(): Result<List<ServiceResponse>> = runCatching {
        val response = api.getAllServices()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to load services (HTTP ${response.code()})")
        }
        body.data.orEmpty()
    }

    suspend fun createService(request: CreateServiceRequest): Result<ServiceResponse> = runCatching {
        val response = api.createService(request)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to create service (HTTP ${response.code()})")
        }
        body.data ?: error("Create response missing data")
    }

    suspend fun getAllOrders(): Result<List<OrderResponse>> = runCatching {
        val response = api.getAllOrders()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to load orders (HTTP ${response.code()})")
        }
        body.data.orEmpty()
    }
}
