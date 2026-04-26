package com.washwise.mobile.feature.profile.presenter

/**
 * MVP contract for the Change Password screen.
 */
interface ChangePasswordContract {

    interface View {
        fun showSubmitting()
        fun hideSubmitting()
        fun showSuccess()
        fun showError(message: String)
        fun showFieldError(field: Field, message: String)
        fun showStrength(level: StrengthLevel)
        fun close()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun onNewPasswordChanged(newPassword: String)
        fun submit(input: ChangeInput)
    }

    data class ChangeInput(
        val current: String,
        val newPassword: String,
        val confirmPassword: String
    )

    enum class Field { CURRENT, NEW, CONFIRM }

    enum class StrengthLevel(val label: String, val colorHex: String) {
        WEAK("Weak", "#DC2626"),
        MEDIUM("Medium", "#D97706"),
        STRONG("Strong", "#16A34A")
    }
}
