package com.washwise.mobile.data.api

// Imports for our newly created Auth models
import com.washwise.mobile.data.model.response.ApiResponse
import com.washwise.mobile.data.model.response.AuthResponse
import com.washwise.mobile.data.model.request.LoginRequest
import com.washwise.mobile.data.model.request.RegisterRequest

// Imports for Claude's profile/order models
import com.washwise.mobile.data.model.request.UpdateProfileRequest
import com.washwise.mobile.data.model.request.ChangePasswordRequest
import com.washwise.mobile.data.model.response.UserResponse

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==========================================
    // Authentication (Using our correct AuthResponse!)
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
    // Orders (for Dashboard)
    // ==========================================
    @GET("orders/my-orders")
    suspend fun getMyOrders(): Response<ApiResponse<List<Any>>>
}