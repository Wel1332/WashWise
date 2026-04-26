package com.washwise.mobile.feature.admin.presenter

import com.washwise.mobile.feature.admin.data.CreateServiceRequest
import com.washwise.mobile.feature.service.data.ServiceResponse

/**
 * MVP contract for the Admin Services tab — list services and create new ones.
 */
interface AdminServicesContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun renderServices(services: List<ServiceResponse>)
        fun showCreating()
        fun hideCreating()
        fun showCreateSuccess()
        fun showError(message: String)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun load()
        fun createService(request: CreateServiceRequest)
    }
}
