package com.washwise.mobile.feature.admin.presenter

import com.washwise.mobile.feature.order.data.OrderResponse

/**
 * MVP contract for the Admin Overview tab — high-level stats, status breakdown
 * and the 5 most recent orders.
 */
interface AdminOverviewContract {

    interface View {
        fun renderStats(stats: Stats)
        fun renderBreakdown(buckets: List<StatusBucket>)
        fun renderRecentOrders(orders: List<OrderResponse>)
        fun showError(message: String)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun load()
    }

    data class Stats(
        val totalOrders: Int,
        val activeOrders: Int,
        val revenue: Double,
        val users: Int
    )

    data class StatusBucket(
        val status: String,
        val label: String,
        val count: Int,
        val percent: Int
    )
}
