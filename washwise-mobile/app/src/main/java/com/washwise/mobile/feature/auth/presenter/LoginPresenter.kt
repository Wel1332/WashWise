package com.washwise.mobile.feature.auth.presenter

import com.washwise.mobile.feature.auth.data.AuthRepository
import com.washwise.mobile.feature.auth.presenter.LoginContract.Field
import com.washwise.mobile.feature.auth.presenter.LoginContract.Role
import com.washwise.mobile.shared.util.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Presenter for the Login screen. Pure JVM — no Android imports — so it can be
 * unit-tested independently of the Activity.
 */
class LoginPresenter(
    private val repository: AuthRepository = AuthRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : LoginContract.Presenter {

    private var view: LoginContract.View? = null
    private var inFlight: Job? = null

    override fun attach(view: LoginContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun start() {
        if (SharedPrefManager.isLoggedIn()) {
            view?.navigateToHome(Role.fromBackend(SharedPrefManager.getUserRole()))
        }
    }

    override fun submit(email: String, password: String) {
        val v = view ?: return
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isEmpty()) {
            v.showFieldError(Field.EMAIL, "Email is required")
            return
        }
        if (trimmedPassword.isEmpty()) {
            v.showFieldError(Field.PASSWORD, "Password is required")
            return
        }

        v.showLoading()
        inFlight = scope.launch {
            repository.login(trimmedEmail, trimmedPassword)
                .onSuccess { auth ->
                    SharedPrefManager.saveAuthSession(
                        token = auth.accessToken,
                        refreshToken = auth.refreshToken,
                        id = auth.id,
                        name = auth.fullName,
                        email = auth.email,
                        role = auth.role
                    )
                    view?.navigateToHome(Role.fromBackend(auth.role))
                }
                .onFailure { error -> view?.showError(error.message ?: "Login failed") }
            view?.hideLoading()
        }
    }

    override fun onRegisterClicked() {
        view?.navigateToRegister()
    }
}
