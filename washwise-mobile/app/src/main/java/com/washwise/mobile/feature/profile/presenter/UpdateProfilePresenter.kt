package com.washwise.mobile.feature.profile.presenter

import com.washwise.mobile.feature.profile.data.ProfileRepository
import com.washwise.mobile.feature.profile.data.UpdateProfileRequest
import com.washwise.mobile.feature.profile.presenter.UpdateProfileContract.Field
import com.washwise.mobile.feature.profile.presenter.UpdateProfileContract.UpdateInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Presenter for Update Profile. Holds no Android references (no Context/View imports)
 * so it is unit-testable in pure JVM.
 */
class UpdateProfilePresenter(
    private val repository: ProfileRepository = ProfileRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : UpdateProfileContract.Presenter {

    private var view: UpdateProfileContract.View? = null
    private var inFlight: Job? = null

    override fun attach(view: UpdateProfileContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun loadProfile() {
        val v = view ?: return
        v.showLoading()
        inFlight = scope.launch {
            repository.getProfile()
                .onSuccess { profile -> view?.renderProfile(profile) }
                .onFailure { error -> view?.showError(error.message ?: "Couldn't load profile") }
            view?.hideLoading()
        }
    }

    override fun save(input: UpdateInput) {
        val v = view ?: return
        if (!validate(input, v)) return

        val request = UpdateProfileRequest(
            fullName = input.fullName.takeIfNotBlank(),
            bio = input.bio.takeIfNotBlank(),
            phoneNumber = input.phoneNumber.takeIfNotBlank(),
            address = input.address.takeIfNotBlank(),
            city = input.city.takeIfNotBlank(),
            zipCode = input.zipCode.takeIfNotBlank()
        )

        v.showSaving()
        inFlight = scope.launch {
            repository.updateProfile(request)
                .onSuccess { view?.showSaveSuccess(); view?.close() }
                .onFailure { error -> view?.showError(error.message ?: "Failed to save profile") }
            view?.hideSaving()
        }
    }

    override fun openChangePassword() {
        // View handles navigation — presenter just signals intent if routing becomes more complex.
    }

    private fun validate(input: UpdateInput, view: UpdateProfileContract.View): Boolean {
        if (input.fullName.isBlank()) {
            view.showFieldError(Field.FULL_NAME, "Full name is required")
            return false
        }
        val phone = input.phoneNumber.trim()
        if (phone.isNotBlank() && !phone.matches(PHONE_REGEX)) {
            view.showFieldError(Field.PHONE, "Enter a valid phone number")
            return false
        }
        val zip = input.zipCode.trim()
        if (zip.isNotBlank() && !zip.all { it.isDigit() }) {
            view.showFieldError(Field.ZIP, "ZIP must be numeric")
            return false
        }
        return true
    }

    private fun String.takeIfNotBlank(): String? =
        trim().takeIf { it.isNotEmpty() }

    companion object {
        private val PHONE_REGEX = Regex("^[+]?[0-9\\s()-]{7,20}$")
    }
}
