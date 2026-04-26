package com.washwise.mobile.feature.profile.presenter

import com.washwise.mobile.feature.profile.data.ChangePasswordRequest
import com.washwise.mobile.feature.profile.data.ProfileRepository
import com.washwise.mobile.feature.profile.presenter.ChangePasswordContract.ChangeInput
import com.washwise.mobile.feature.profile.presenter.ChangePasswordContract.Field
import com.washwise.mobile.feature.profile.presenter.ChangePasswordContract.StrengthLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Presenter for Change Password. Pure JVM — no Android imports.
 */
class ChangePasswordPresenter(
    private val repository: ProfileRepository = ProfileRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : ChangePasswordContract.Presenter {

    private var view: ChangePasswordContract.View? = null
    private var inFlight: Job? = null

    override fun attach(view: ChangePasswordContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun onNewPasswordChanged(newPassword: String) {
        val level = evaluateStrength(newPassword) ?: return
        view?.showStrength(level)
    }

    override fun submit(input: ChangeInput) {
        val v = view ?: return
        if (!validate(input, v)) return

        val request = ChangePasswordRequest(
            currentPassword = input.current,
            newPassword = input.newPassword
        )

        v.showSubmitting()
        inFlight = scope.launch {
            repository.changePassword(request)
                .onSuccess { view?.showSuccess(); view?.close() }
                .onFailure { error ->
                    view?.showError(error.message ?: "Failed to update password")
                }
            view?.hideSubmitting()
        }
    }

    private fun validate(input: ChangeInput, view: ChangePasswordContract.View): Boolean {
        if (input.current.isBlank()) {
            view.showFieldError(Field.CURRENT, "Current password is required")
            return false
        }
        if (input.newPassword.length < MIN_LENGTH) {
            view.showFieldError(Field.NEW, "New password must be at least $MIN_LENGTH characters")
            return false
        }
        if (input.newPassword == input.current) {
            view.showFieldError(Field.NEW, "New password must differ from the current one")
            return false
        }
        if (input.confirmPassword != input.newPassword) {
            view.showFieldError(Field.CONFIRM, "Passwords don't match")
            return false
        }
        return true
    }

    private fun evaluateStrength(password: String): StrengthLevel? {
        if (password.isEmpty()) return null
        var score = 0
        if (password.length >= MIN_LENGTH) score++
        if (password.length >= 12) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { it.isLetter() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        return when {
            score <= 2 -> StrengthLevel.WEAK
            score <= 3 -> StrengthLevel.MEDIUM
            else -> StrengthLevel.STRONG
        }
    }

    companion object {
        private const val MIN_LENGTH = 8
    }
}
