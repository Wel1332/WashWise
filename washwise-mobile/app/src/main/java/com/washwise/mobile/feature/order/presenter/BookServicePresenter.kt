package com.washwise.mobile.feature.order.presenter

import com.washwise.mobile.feature.order.data.CreateOrderRequest
import com.washwise.mobile.feature.order.data.OrderRepository
import com.washwise.mobile.feature.order.presenter.BookServiceContract.BookingInput
import com.washwise.mobile.feature.order.presenter.BookServiceContract.Service
import com.washwise.mobile.feature.service.data.ServiceRepository
import com.washwise.mobile.feature.service.data.ServiceResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Presenter for the Book Service screen.
 *
 * - Locates the backend ServiceResponse that matches the preset passed from the
 * dashboard (by id, then by name) so we know the service id to submit.
 * - Recomputes the running total whenever weight changes.
 * - Validates the booking form before calling the repository.
 */
class BookServicePresenter(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val serviceRepository: ServiceRepository = ServiceRepository(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : BookServiceContract.Presenter {

    private var view: BookServiceContract.View? = null
    private var inFlight: Job? = null
    private var resolvedService: Service? = null
    private var currentWeight = 0.0

    override fun attach(view: BookServiceContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
        inFlight?.cancel()
        scope.cancel()
    }

    override fun load(presetId: String?, presetName: String?) {
        val v = view ?: return

        // Render a local-first snapshot so the screen never looks empty.
        val preset = PresetService.fromName(presetName)
        preset?.let { resolvedService = it.toService(id = null) }
        resolvedService?.let { v.renderService(it) }

        v.showServicesLoading()
        inFlight = scope.launch {
            serviceRepository.getActiveServices()
                .onSuccess { services ->
                    val match = services.findMatch(presetId, presetName)
                    resolvedService = match?.let { Service.fromBackend(it, preset) }
                        ?: resolvedService

                    // Safely update UI with results
                    withContext(Dispatchers.Main) {
                        resolvedService?.let { view?.renderService(it) }
                        if (match == null && services.isNotEmpty() && presetName != null) {
                            view?.showServiceUnavailable(
                                "'$presetName' isn't available. Available: ${services.joinToString { it.name }}"
                            )
                        }
                        view?.updateTotal(currentTotal())
                    }
                }
                .onFailure { error ->
                    // Safely show error on UI
                    withContext(Dispatchers.Main) {
                        view?.showServiceUnavailable(error.message ?: "Couldn't load services")
                    }
                }

            // Safely hide loading indicator on UI
            withContext(Dispatchers.Main) {
                view?.hideServicesLoading()
            }
        }
    }

    override fun onWeightChanged(weight: Double) {
        currentWeight = weight.coerceIn(0.0, MAX_WEIGHT)
        view?.updateTotal(currentTotal())
    }

    override fun submit(input: BookingInput) {
        val v = view ?: return
        val service = resolvedService
        val serviceId = service?.id

        if (serviceId.isNullOrBlank()) {
            v.showError("Missing service. Please go back and pick one.")
            return
        }
        if (input.weightKg <= 0.0) {
            v.showError("Please enter a weight greater than 0")
            return
        }
        val pickupDate = input.pickupDateIso
        if (pickupDate.isNullOrBlank()) {
            v.showError("Please select a pickup date")
            return
        }
        val pickupSlot = input.pickupSlotId
        if (pickupSlot.isNullOrBlank()) {
            v.showError("Please select a pickup time")
            return
        }
        if (input.address.isBlank()) {
            v.showError("Please enter your pickup address")
            return
        }
        val deliveryDate = input.deliveryDateIso
        if (deliveryDate.isNullOrBlank()) {
            v.showError("Please select a delivery date")
            return
        }
        val deliverySlot = input.deliverySlotId
        if (deliverySlot.isNullOrBlank()) {
            v.showError("Please select a delivery time")
            return
        }

        val request = CreateOrderRequest(
            serviceId = serviceId,
            totalPrice = service.pricePerKg * input.weightKg,
            pickupAddress = input.address.trim(),
            pickupDate = pickupDate,
            pickupTimeSlot = pickupSlot,
            deliveryDate = deliveryDate,
            deliveryTimeSlot = deliverySlot,
            weightKg = input.weightKg,
            specialInstructions = input.specialInstructions.trim().ifBlank { null },
            status = "PENDING"
        )

        v.showSubmitting()
        inFlight = scope.launch {
            orderRepository.createOrder(request)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        view?.showSuccess()
                        view?.close()
                    }
                }
                .onFailure { error ->
                    withContext(Dispatchers.Main) {
                        view?.showError(error.message ?: "Failed to book")
                    }
                }

            withContext(Dispatchers.Main) {
                view?.hideSubmitting()
            }
        }
    }

    private fun currentTotal(): Double =
        (resolvedService?.pricePerKg ?: 0.0) * currentWeight

    private fun List<ServiceResponse>.findMatch(presetId: String?, presetName: String?): ServiceResponse? {
        if (!presetId.isNullOrBlank()) {
            firstOrNull { it.id == presetId }?.let { return it }
        }
        if (presetName.isNullOrBlank()) return null
        val normalized = presetName.normalize()
        return firstOrNull { it.name.normalize() == normalized }
            ?: firstOrNull { it.name.normalize().contains(normalized) }
            ?: firstOrNull { normalized.contains(it.name.normalize()) }
    }

    private fun String.normalize(): String =
        lowercase().replace(" ", "").replace("-", "")

    private fun PresetService.toService(id: String?): Service = Service(
        id = id,
        name = name,
        description = description,
        pricePerKg = pricePerKg,
        minDeliveryDays = minDeliveryDays,
        iconRes = iconRes
    )

    companion object {
        private const val MAX_WEIGHT = 50.0
    }
}