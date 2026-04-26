package com.washwise.mobile.feature.auth.presenter

/**
 * MVP contract for the Login screen. See [LoginPresenter] for the implementation.
 */
interface LoginContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showFieldError(field: Field, message: String)
        fun showError(message: String)
        fun navigateToHome(role: Role)
        fun navigateToRegister()
    }

    /** Maps backend role strings (`CUSTOMER`, `STAFF`, `ADMIN`) onto routing decisions. */
    enum class Role {
        CUSTOMER, STAFF, ADMIN;

        companion object {
            fun fromBackend(value: String?): Role = when ((value ?: "").uppercase()) {
                "ADMIN" -> ADMIN
                "STAFF" -> STAFF
                else -> CUSTOMER
            }
        }
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun start()
        fun submit(email: String, password: String)
        fun onRegisterClicked()
    }

    enum class Field { EMAIL, PASSWORD }
}
