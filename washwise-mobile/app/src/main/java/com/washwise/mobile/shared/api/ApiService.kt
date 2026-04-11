package com.washwise.mobile.shared.api

// Auth models
import com.washwise.mobile.shared.model.ApiResponse
import com.washwise.mobile.feature.auth.data.AuthResponse
import com.washwise.mobile.feature.auth.data.LoginRequest
import com.washwise.mobile.feature.auth.data.RegisterRequest

// Profile models
import com.washwise.mobile.feature.profile.data.UpdateProfileRequest
import com.washwise.mobile.feature.profile.data.ChangePasswordRequest
import com.washwise.mobile.feature.profile.data.UserResponse

// Order models
import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.feature.order.data.CreateOrderRequest

// Service models
import com.washwise.mobile.feature.service.data.ServiceResponse

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==========================================
    // Authentication
    // ==========================================
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Any>>

    // ==========================================
    // Profile
    // ==========================================
    @GET("profile")
    suspend fun getProfile(): Response<ApiResponse<UserResponse>>

    @PUT("profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserResponse>>

    @POST("profile/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Any>>

    // ==========================================
    // Orders
    // ==========================================
    @GET("orders/my-orders")
    suspend fun getMyOrders(): Response<ApiResponse<List<OrderResponse>>>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<OrderResponse>>

    @DELETE("orders/{id}")
    suspend fun cancelOrder(@Path("id") id: String): Response<ApiResponse<Any>>

    // ==========================================
    // Services
    // ==========================================
    @GET("services/active")
    suspend fun getActiveServices(): Response<ApiResponse<List<ServiceResponse>>>
}