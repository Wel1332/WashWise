package com.washwise.mobile.feature.profile.presenter

import com.washwise.mobile.feature.profile.data.UserResponse

/**
 * MVP contract for the Update Profile screen.
 *
 * The View is passive — it renders what the Presenter tells it and forwards user input.
 * The Presenter owns coordination with the repository / API layer.
 */
interface UpdateProfileContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showSaving()
        fun hideSaving()
        fun renderProfile(profile: UserResponse)
        fun showSaveSuccess()
        fun showError(message: String)
        fun showFieldError(field: Field, message: String)
        fun close()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun loadProfile()
        fun save(input: UpdateInput)
        fun openChangePassword()
    }

    data class UpdateInput(
        val fullName: String,
        val phoneNumber: String,
        val bio: String,
        val address: String,
        val city: String,
        val zipCode: String
    )

    enum class Field { FULL_NAME, PHONE, ADDRESS, CITY, ZIP, BIO }
}
