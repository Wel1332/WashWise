package com.washwise.mobile.feature.dashboard.presenter

import com.washwise.mobile.feature.dashboard.ui.DashboardService
import com.washwise.mobile.feature.order.data.OrderResponse

/**
 * MVP contract for the Home / Dashboard screen.
 */
interface DashboardContract {

    interface View {
        fun renderGreeting(timeOfDay: String, firstName: String)
        fun renderServices(services: List<DashboardService>)
        fun renderActiveOrder(order: OrderResponse?)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun start()
    }
}
