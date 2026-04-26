package com.washwise.mobile.feature.auth.presenter

import com.washwise.mobile.feature.auth.data.AuthRepository
import com.washwise.mobile.feature.auth.presenter.RegisterContract.Field
import com.washwise.mobile.feature.auth.presenter.RegisterContract.RegisterInput
import com.washwise.mobile.shared.util.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Presenter for the Register screen. Validates input, invokes the repository
 * and persists the returned session.
 */
class RegisterPresenter(
    private val repository: AuthRepository = AuthRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : RegisterContract.Presenter {

    private var view: RegisterContract.View? = null
    private var inFlight: Job? = null

    override fun attach(view: RegisterContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun submit(input: RegisterInput) {
        val v = view ?: return
        if (!validate(input, v)) return

        v.showLoading()
        inFlight = scope.launch {
            repository.register(
                fullName = input.fullName.trim(),
                email = input.email.trim(),
                password = input.password,
                confirmPassword = input.confirmPassword
            ).onSuccess { auth ->
                SharedPrefManager.saveAuthSession(
                    token = auth.accessToken,
                    refreshToken = auth.refreshToken,
                    id = auth.id,
                    name = auth.fullName,
                    email = auth.email,
                    role = auth.role
                )
                view?.navigateToHome()
            }.onFailure { error ->
                view?.showError(error.message ?: "Registration failed")
            }
            view?.hideLoading()
        }
    }

    private fun validate(input: RegisterInput, view: RegisterContract.View): Boolean {
        if (input.fullName.isBlank()) {
            view.showFieldError(Field.FULL_NAME, "Full name is required")
            return false
        }
        if (!EMAIL_REGEX.matches(input.email.trim())) {
            view.showFieldError(Field.EMAIL, "Enter a valid email")
            return false
        }
        if (input.password.length < MIN_PASSWORD_LENGTH) {
            view.showFieldError(Field.PASSWORD, "Password must be at least $MIN_PASSWORD_LENGTH characters")
            return false
        }
        if (input.password != input.confirmPassword) {
            view.showFieldError(Field.CONFIRM_PASSWORD, "Passwords do not match")
            return false
        }
        return true
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
