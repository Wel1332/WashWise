package com.washwise.mobile.feature.order.presenter

import com.washwise.mobile.feature.order.data.OrderResponse

/**
 * MVP contract for the Orders (history) screen.
 */
interface OrdersContract {

    interface View {
        fun showLoading()
        fun showEmpty()
        fun showError(message: String)
        fun showOrders(active: List<OrderResponse>, completed: List<OrderResponse>)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun load()
    }
}
