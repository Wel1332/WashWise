package com.washwise.mobile.feature.auth.presenter

/**
 * MVP contract for the Register screen.
 */
interface RegisterContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showFieldError(field: Field, message: String)
        fun showError(message: String)
        fun navigateToHome()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun submit(input: RegisterInput)
    }

    data class RegisterInput(
        val fullName: String,
        val email: String,
        val password: String,
        val confirmPassword: String
    )

    enum class Field { FULL_NAME, EMAIL, PASSWORD, CONFIRM_PASSWORD }
}
