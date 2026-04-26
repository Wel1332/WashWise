package com.washwise.mobile.feature.admin.presenter

import com.washwise.mobile.feature.admin.data.AdminRepository
import com.washwise.mobile.feature.admin.data.CreateServiceRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Presenter for the Admin Services tab.
 */
class AdminServicesPresenter(
    private val repository: AdminRepository = AdminRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : AdminServicesContract.Presenter {

    private var view: AdminServicesContract.View? = null
    private var inFlight: Job? = null

    override fun attach(view: AdminServicesContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun load() {
        val v = view ?: return
        v.showLoading()
        inFlight = scope.launch {
            repository.getAllServices()
                .onSuccess { services -> view?.renderServices(services) }
                .onFailure { error -> view?.showError(error.message ?: "Couldn't load services") }
            view?.hideLoading()
        }
    }

    override fun createService(request: CreateServiceRequest) {
        val v = view ?: return
        if (!validate(request, v)) return

        v.showCreating()
        inFlight = scope.launch {
            repository.createService(request)
                .onSuccess {
                    view?.showCreateSuccess()
                    load()
                }
                .onFailure { error -> view?.showError(error.message ?: "Failed to create service") }
            view?.hideCreating()
        }
    }

    private fun validate(request: CreateServiceRequest, view: AdminServicesContract.View): Boolean {
        if (request.name.length !in 3..100) {
            view.showError("Name must be 3–100 characters")
            return false
        }
        if (request.description.length !in 10..500) {
            view.showError("Description must be 10–500 characters")
            return false
        }
        if (request.price <= 0.0) {
            view.showError("Price must be greater than 0")
            return false
        }
        if (request.category.length !in 3..50) {
            view.showError("Category must be 3–50 characters")
            return false
        }
        if (request.duration.length > 50) {
            view.showError("Duration must be 50 characters or less")
            return false
        }
        return true
    }
}
