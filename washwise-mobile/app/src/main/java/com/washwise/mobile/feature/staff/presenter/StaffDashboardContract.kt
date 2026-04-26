package com.washwise.mobile.feature.staff.presenter

import com.washwise.mobile.feature.order.data.OrderResponse

/**
 * MVP contract for the Staff Dashboard. Shows queue stats + a filterable list
 * of all orders, and lets a staff user advance the order status pipeline.
 *
 * Status flow: PENDING → RECEIVED → WASHING → DRYING → READY → COMPLETED.
 */
interface StaffDashboardContract {

    interface View {
        fun renderStats(stats: Stats)
        fun renderFilterCounts(counts: Map<Filter, Int>)
        fun renderOrders(orders: List<OrderResponse>)
        fun showError(message: String)
        fun showStatusUpdated(orderId: String, newStatus: String)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun load()
        fun setFilter(filter: Filter)
        fun advance(order: OrderResponse)
    }

    data class Stats(
        val total: Int,
        val active: Int,
        val completed: Int,
        val pending: Int
    )

    enum class Filter(val backendValue: String?) {
        ALL(null),
        PENDING("PENDING"),
        RECEIVED("RECEIVED"),
        WASHING("WASHING"),
        DRYING("DRYING"),
        READY("READY"),
        COMPLETED("COMPLETED"),
        CANCELLED("CANCELLED")
    }
}
