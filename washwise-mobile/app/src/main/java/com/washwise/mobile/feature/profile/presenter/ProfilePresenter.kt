package com.washwise.mobile.feature.profile.presenter

import com.washwise.mobile.feature.order.data.OrderRepository
import com.washwise.mobile.feature.profile.data.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Presenter for the Profile tab. Combines user details + order stats.
 */
class ProfilePresenter(
    private val profileRepository: ProfileRepository = ProfileRepository(),
    private val orderRepository: OrderRepository = OrderRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : ProfileContract.Presenter {

    private var view: ProfileContract.View? = null
    private var inFlight: Job? = null

    override fun attach(view: ProfileContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun load() {
        inFlight = scope.launch {
            profileRepository.getProfile()
                .onSuccess { profile ->
                    view?.renderProfile(profile, initialsOf(profile.fullName))
                }

            orderRepository.getMyOrders()
                .onSuccess { orders ->
                    val completed = orders.count {
                        (it.status ?: "").uppercase() in COMPLETED_STATUSES
                    }
                    view?.renderStats(orders.size, completed, DEFAULT_RATING)
                }
                .onFailure {
                    view?.renderStats(0, 0, DEFAULT_RATING)
                }
        }
    }

    private fun initialsOf(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "U"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts[0].first()}${parts[1].first()}".uppercase()
        }
    }

    companion object {
        private const val DEFAULT_RATING = "4.9"
        private val COMPLETED_STATUSES = setOf("COMPLETED", "DELIVERED")
    }
}
