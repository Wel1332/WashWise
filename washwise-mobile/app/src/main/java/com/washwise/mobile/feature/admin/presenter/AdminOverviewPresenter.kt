package com.washwise.mobile.feature.admin.presenter

import com.washwise.mobile.feature.admin.data.AdminRepository
import com.washwise.mobile.feature.admin.presenter.AdminOverviewContract.Stats
import com.washwise.mobile.feature.admin.presenter.AdminOverviewContract.StatusBucket
import com.washwise.mobile.feature.order.data.OrderResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Presenter for the Admin Overview tab. Loads orders + users in parallel and
 * derives stats / breakdown / recent list in pure Kotlin.
 */
class AdminOverviewPresenter(
    private val repository: AdminRepository = AdminRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : AdminOverviewContract.Presenter {

    private var view: AdminOverviewContract.View? = null
    private var inFlight: Job? = null

    override fun attach(view: AdminOverviewContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun load() {
        inFlight = scope.launch {
            val ordersDeferred = async { repository.getAllOrders() }
            val usersDeferred = async { repository.getAllUsers() }

            val orders = ordersDeferred.await().getOrElse { emptyList() }
            val users = usersDeferred.await().getOrElse { emptyList() }

            view?.renderStats(buildStats(orders, users.size))
            view?.renderBreakdown(buildBreakdown(orders))
            view?.renderRecentOrders(orders.take(MAX_RECENT))
        }
    }

    private fun buildStats(orders: List<OrderResponse>, userCount: Int): Stats {
        val active = orders.count { (it.status ?: "").uppercase() in ACTIVE_STATUSES }
        val revenue = orders.sumOf { it.totalPrice ?: 0.0 }
        return Stats(
            totalOrders = orders.size,
            activeOrders = active,
            revenue = revenue,
            users = userCount
        )
    }

    private fun buildBreakdown(orders: List<OrderResponse>): List<StatusBucket> {
        val total = orders.size.coerceAtLeast(1)
        return BREAKDOWN_STATUSES.map { (status, label) ->
            val count = orders.count { (it.status ?: "").uppercase() == status }
            val percent = (count * 100f / total).toInt().coerceIn(0, 100)
            StatusBucket(status, label, count, percent)
        }
    }

    companion object {
        private const val MAX_RECENT = 5

        private val ACTIVE_STATUSES = setOf(
            "PENDING", "RECEIVED", "WASHING", "DRYING", "READY"
        )

        private val BREAKDOWN_STATUSES = listOf(
            "PENDING" to "Pending",
            "RECEIVED" to "Received",
            "WASHING" to "Washing",
            "DRYING" to "Drying",
            "READY" to "Ready",
            "COMPLETED" to "Completed"
        )
    }
}
