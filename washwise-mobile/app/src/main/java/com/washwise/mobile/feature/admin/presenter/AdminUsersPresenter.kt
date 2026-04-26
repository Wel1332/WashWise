package com.washwise.mobile.feature.admin.presenter

import com.washwise.mobile.feature.admin.data.AdminRepository
import com.washwise.mobile.shared.util.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Presenter for the Admin Users tab.
 */
class AdminUsersPresenter(
    private val repository: AdminRepository = AdminRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : AdminUsersContract.Presenter {

    private var view: AdminUsersContract.View? = null
    private var inFlight: Job? = null

    override fun attach(view: AdminUsersContract.View) {
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
            repository.getAllUsers()
                .onSuccess { users ->
                    view?.renderUsers(users, currentUserId())
                }
                .onFailure { error -> view?.showError(error.message ?: "Couldn't load users") }
            view?.hideLoading()
        }
    }

    override fun changeRole(userId: String, newRole: String) {
        scope.launch {
            repository.updateUserRole(userId, newRole)
                .onSuccess { user ->
                    view?.showRoleUpdated(user)
                    load()
                }
                .onFailure { error ->
                    view?.showError(error.message ?: "Failed to update role")
                }
        }
    }

    private fun currentUserId(): String? = SharedPrefManager.getUserId()
}
