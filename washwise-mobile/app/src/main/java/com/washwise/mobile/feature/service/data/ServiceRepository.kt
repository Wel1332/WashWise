package com.washwise.mobile.feature.service.data

import com.washwise.mobile.shared.api.ApiService
import com.washwise.mobile.shared.api.RetrofitClient

/**
 * Data boundary for the service catalog.
 */
class ServiceRepository(
    private val api: ApiService = RetrofitClient.instance
) {

    suspend fun getActiveServices(): Result<List<ServiceResponse>> = runCatching {
        val response = api.getActiveServices()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to load services (HTTP ${response.code()})")
        }
        body.data.orEmpty()
    }
}
