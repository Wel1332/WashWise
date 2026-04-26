package com.washwise.mobile.feature.admin.presenter

import com.washwise.mobile.feature.admin.data.AdminUser

/**
 * MVP contract for the Admin Users tab — list users and let admin edit roles.
 */
interface AdminUsersContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun renderUsers(users: List<AdminUser>, currentUserId: String?)
        fun showRoleUpdated(user: AdminUser)
        fun showError(message: String)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun load()
        fun changeRole(userId: String, newRole: String)
    }
}
