package com.washwise.mobile.feature.staff.presenter

import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.feature.staff.data.StaffRepository
import com.washwise.mobile.feature.staff.presenter.StaffDashboardContract.Filter
import com.washwise.mobile.feature.staff.presenter.StaffDashboardContract.Stats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Presenter for the Staff Dashboard. Holds the full order list in memory and
 * derives stats / filtered views from it so filter switches don't re-hit the API.
 */
class StaffDashboardPresenter(
    private val repository: StaffRepository = StaffRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : StaffDashboardContract.Presenter {

    private var view: StaffDashboardContract.View? = null
    private var inFlight: Job? = null
    private var allOrders: List<OrderResponse> = emptyList()
    private var currentFilter: Filter = Filter.ALL

    override fun attach(view: StaffDashboardContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun load() {
        inFlight = scope.launch {
            repository.getAllOrders()
                .onSuccess { orders ->
                    allOrders = orders
                    publish()
                }
                .onFailure { error ->
                    view?.showError(error.message ?: "Couldn't load orders")
                }
        }
    }

    override fun setFilter(filter: Filter) {
        currentFilter = filter
        view?.renderOrders(filtered())
    }

    override fun advance(order: OrderResponse) {
        val next = nextStatus(order.status) ?: return
        scope.launch {
            repository.updateStatus(order.id, next)
                .onSuccess {
                    view?.showStatusUpdated(order.id, next)
                    load()
                }
                .onFailure { error ->
                    view?.showError(error.message ?: "Couldn't update order")
                }
        }
    }

    private fun publish() {
        view?.renderStats(buildStats(allOrders))
        view?.renderFilterCounts(buildCounts(allOrders))
        view?.renderOrders(filtered())
    }

    private fun filtered(): List<OrderResponse> = allOrders.filter { matches(it, currentFilter) }

    private fun matches(order: OrderResponse, filter: Filter): Boolean {
        if (filter == Filter.ALL) return true
        return (order.status ?: "").uppercase() == filter.backendValue
    }

    private fun buildStats(orders: List<OrderResponse>): Stats {
        val active = orders.count { (it.status ?: "").uppercase() in ACTIVE_STATUSES }
        val completed = orders.count { (it.status ?: "").uppercase() == "COMPLETED" }
        val pending = orders.count { (it.status ?: "").uppercase() == "PENDING" }
        return Stats(total = orders.size, active = active, completed = completed, pending = pending)
    }

    private fun buildCounts(orders: List<OrderResponse>): Map<Filter, Int> {
        val counts = mutableMapOf<Filter, Int>()
        Filter.values().forEach { counts[it] = 0 }
        counts[Filter.ALL] = orders.size
        orders.forEach { order ->
            val status = (order.status ?: "").uppercase()
            Filter.values()
                .firstOrNull { it.backendValue == status }
                ?.let { counts[it] = (counts[it] ?: 0) + 1 }
        }
        return counts
    }

    private fun nextStatus(current: String?): String? = when ((current ?: "").uppercase()) {
        "PENDING" -> "RECEIVED"
        "RECEIVED" -> "WASHING"
        "WASHING" -> "DRYING"
        "DRYING" -> "READY"
        "READY" -> "COMPLETED"
        else -> null
    }

    companion object {
        private val ACTIVE_STATUSES = setOf(
            "PENDING", "RECEIVED", "WASHING", "DRYING", "READY"
        )
    }
}
