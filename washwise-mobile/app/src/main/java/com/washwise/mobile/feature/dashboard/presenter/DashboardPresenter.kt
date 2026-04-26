package com.washwise.mobile.feature.dashboard.presenter

import com.washwise.mobile.R
import com.washwise.mobile.feature.dashboard.ui.DashboardService
import com.washwise.mobile.feature.order.data.OrderRepository
import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.feature.service.data.ServiceRepository
import com.washwise.mobile.feature.service.data.ServiceResponse
import com.washwise.mobile.shared.util.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

/**
 * Presenter for the Dashboard. Composes greeting + service tiles + active order.
 */
class DashboardPresenter(
    private val serviceRepository: ServiceRepository = ServiceRepository(),
    private val orderRepository: OrderRepository = OrderRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    private val now: () -> Calendar = { Calendar.getInstance() }
) : DashboardContract.Presenter {

    private var view: DashboardContract.View? = null
    private var inFlight: Job? = null

    override fun attach(view: DashboardContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun start() {
        renderGreeting()
        renderTilesFromPresets()
        inFlight = scope.launch {
            val servicesDeferred = async { serviceRepository.getActiveServices() }
            val ordersDeferred = async { orderRepository.getMyOrders() }

            val services = servicesDeferred.await().getOrElse { emptyList() }
            view?.renderServices(buildTiles(services))

            val orders = ordersDeferred.await().getOrElse { emptyList() }
            view?.renderActiveOrder(findActiveOrder(orders))
        }
    }

    private fun renderGreeting() {
        val hour = now().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
        val firstName = SharedPrefManager.getUserName()
            ?.trim()
            ?.split(" ")
            ?.firstOrNull()
            ?.takeIf { it.isNotBlank() }
            ?: "there"
        view?.renderGreeting(greeting, firstName)
    }

    private fun renderTilesFromPresets() {
        view?.renderServices(buildTiles(emptyList()))
    }

    private fun buildTiles(apiServices: List<ServiceResponse>): List<DashboardService> {
        fun match(vararg aliases: String): ServiceResponse? {
            val lowered = aliases.map { it.lowercase() }.toSet()
            return apiServices.firstOrNull { it.name.lowercase() in lowered }
        }

        fun price(s: ServiceResponse?, fallback: Double): String =
            String.format(Locale.US, "From ₱%.0f/kg", s?.price ?: fallback)

        val washOnly = match("wash only")
        val washDryFold = match("wash-dry-fold", "wash dry fold", "wash & fold")
        val dryClean = match("dry cleaning", "dry clean")
        val premium = match("premium care", "premium")

        return listOf(
            DashboardService(
                backendId = washOnly?.id,
                name = "Wash Only",
                caption = price(washOnly, 30.0),
                iconRes = R.drawable.ic_droplet,
                iconTint = "#0891B2",
                bgTint = "#CFFAFE"
            ),
            DashboardService(
                backendId = washDryFold?.id,
                name = "Wash-Dry-Fold",
                caption = price(washDryFold, 40.0),
                iconRes = R.drawable.ic_tshirt,
                iconTint = "#2563EB",
                bgTint = "#DBEAFE"
            ),
            DashboardService(
                backendId = dryClean?.id,
                name = "Dry Cleaning",
                caption = price(dryClean, 150.0),
                iconRes = R.drawable.ic_sparkle,
                iconTint = "#9810FA",
                bgTint = "#F3E8FF"
            ),
            DashboardService(
                backendId = premium?.id,
                name = "Premium Care",
                caption = price(premium, 175.0),
                iconRes = R.drawable.ic_star,
                iconTint = "#FF6B35",
                bgTint = "#FFEDD5"
            )
        )
    }

    private fun findActiveOrder(orders: List<OrderResponse>): OrderResponse? =
        orders.firstOrNull {
            val status = (it.status ?: "").uppercase()
            status !in TERMINAL_STATUSES
        }

    companion object {
        private val TERMINAL_STATUSES = setOf(
            "DELIVERED", "COMPLETED", "CANCELLED", "CANCELED"
        )
    }
}
