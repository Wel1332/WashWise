package com.washwise.mobile.feature.profile.data

import com.washwise.mobile.shared.api.ApiService
import com.washwise.mobile.shared.api.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Single data boundary for the profile slice. Presenters depend on this — never
 * on Retrofit directly — so networking concerns stay out of the presentation layer.
 */
class ProfileRepository(
    private val api: ApiService = RetrofitClient.instance
) {

    suspend fun getProfile(): Result<UserResponse> = runCatching {
        val response = api.getProfile()
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to load profile (HTTP ${response.code()})")
        }
        body.data ?: error("Profile response missing data")
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserResponse> = runCatching {
        val response = api.updateProfile(request)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to update profile (HTTP ${response.code()})")
        }
        body.data ?: error("Update response missing data")
    }

    suspend fun uploadProfileImage(
        bytes: ByteArray,
        mimeType: String,
        filename: String
    ): Result<UserResponse> = runCatching {
        val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", filename, body)
        val response = api.uploadProfileImage(part)
        val payload = response.body()
        if (!response.isSuccessful || payload?.success != true) {
            error(payload?.message ?: "Failed to upload image (HTTP ${response.code()})")
        }
        payload.data ?: error("Upload response missing data")
    }

    suspend fun changePassword(request: ChangePasswordRequest): Result<Unit> = runCatching {
        val response = api.changePassword(request)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            error(body?.message ?: "Failed to update password (HTTP ${response.code()})")
        }
    }
}
