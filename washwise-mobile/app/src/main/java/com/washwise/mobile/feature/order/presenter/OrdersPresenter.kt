package com.washwise.mobile.feature.order.presenter

import com.washwise.mobile.feature.order.data.OrderRepository
import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.shared.util.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Presenter for the Orders history screen.
 */
class OrdersPresenter(
    private val repository: OrderRepository = OrderRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : OrdersContract.Presenter {

    private var view: OrdersContract.View? = null
    private var inFlight: Job? = null

    override fun attach(view: OrdersContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun load() {
        val v = view ?: return

        if (SharedPrefManager.getToken().isNullOrBlank()) {
            v.showError("You're not signed in. Please log in again.")
            return
        }

        v.showLoading()
        inFlight = scope.launch {
            repository.getMyOrders()
                .onSuccess { orders ->
                    // Switch to the Main thread to update the UI safely
                    withContext(Dispatchers.Main) {
                        if (orders.isEmpty()) {
                            view?.showEmpty()
                        } else {
                            val (active, completed) = orders.partition { isActive(it.status) }
                            view?.showOrders(active, completed)
                        }
                    }
                }
                .onFailure { error ->
                    // Switch to the Main thread to show the error
                    withContext(Dispatchers.Main) {
                        view?.showError(error.message ?: "Couldn't load orders")
                    }
                }
        }
    }

    private fun isActive(status: String?): Boolean {
        val s = (status ?: "").uppercase()
        return s !in TERMINAL
    }

    companion object {
        private val TERMINAL = setOf("COMPLETED", "DELIVERED", "CANCELLED", "CANCELED")
    }
}