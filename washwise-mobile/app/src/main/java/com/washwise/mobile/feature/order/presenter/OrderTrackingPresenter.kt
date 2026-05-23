package com.washwise.mobile.feature.order.presenter

import com.washwise.mobile.feature.order.data.OrderRepository
import com.washwise.mobile.feature.order.presenter.OrderTrackingContract.Step
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Presenter for the Order Tracking screen.
 */
class OrderTrackingPresenter(
    private val repository: OrderRepository = OrderRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : OrderTrackingContract.Presenter {

    private var view: OrderTrackingContract.View? = null
    private var inFlight: Job? = null
    private var currentOrderId: String? = null
    private var currentStatus: String? = null

    override fun attach(view: OrderTrackingContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun load(orderId: String) {
        val v = view ?: return
        currentOrderId = orderId
        v.showLoading()
        inFlight = scope.launch {
            repository.getMyOrders()
                .onSuccess { orders ->
                    val order = orders.firstOrNull { it.id == orderId }
                    if (order == null) {
                        view?.showError("Order not found")
                        view?.close()
                    } else {
                        renderOrder(order)
                    }
                }
                .onFailure { error ->
                    view?.showError(error.message ?: "Couldn't load order")
                }
            view?.hideLoading()
        }
    }

    override fun onCancelClicked() {
        if (canCancel(currentStatus)) view?.confirmCancel()
    }

    override fun confirmCancel() {
        val v = view ?: return
        val orderId = currentOrderId ?: return
        if (!canCancel(currentStatus)) return
        v.showCancelling()
        inFlight = scope.launch {
            repository.cancelOrder(orderId)
                .onSuccess { order ->
                    renderOrder(order)
                    view?.showCancelSuccess()
                }
                .onFailure { error ->
                    view?.showError(error.message ?: "Couldn't cancel order")
                }
            view?.hideCancelling()
        }
    }

    private fun renderOrder(order: com.washwise.mobile.feature.order.data.OrderResponse) {
        currentStatus = order.status
        val stepIndex = currentStepIndex(order.status)
        val percent = ((stepIndex + 1) * 100f / Step.values().size).toInt()
        view?.renderOrder(order, stepIndex, percent.coerceIn(0, 100))
        view?.setCancelAvailable(canCancel(order.status))
    }

    private fun canCancel(status: String?): Boolean =
        (status ?: "").uppercase() !in TERMINAL_STATUSES

    companion object {
        private val TERMINAL_STATUSES = setOf("COMPLETED", "DELIVERED", "CANCELLED", "CANCELED")
    }

    private fun currentStepIndex(status: String?): Int = when ((status ?: "").uppercase()) {
        "PENDING" -> Step.ORDER_PLACED.ordinal
        "PICKED_UP", "PICKED-UP" -> Step.PICKED_UP.ordinal
        "WASHING", "IN_PROGRESS", "IN-PROGRESS", "PROCESSING" -> Step.IN_WASH.ordinal
        "DRYING" -> Step.DRYING.ordinal
        "READY", "READY_FOR_PICKUP", "READY-FOR-PICKUP" -> Step.READY.ordinal
        "DELIVERED", "COMPLETED" -> Step.DELIVERED.ordinal
        else -> Step.ORDER_PLACED.ordinal
    }
}
