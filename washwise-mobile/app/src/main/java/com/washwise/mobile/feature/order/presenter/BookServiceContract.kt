package com.washwise.mobile.feature.order.presenter

import com.washwise.mobile.feature.service.data.ServiceResponse

/**
 * MVP contract for the Book Service screen.
 */
interface BookServiceContract {

    interface View {
        fun showServicesLoading()
        fun hideServicesLoading()
        fun renderService(service: Service)
        fun showServiceUnavailable(message: String)

        fun showSubmitting()
        fun hideSubmitting()
        fun showSuccess()
        fun showError(message: String)

        fun updateTotal(total: Double)
        fun close()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun load(presetId: String?, presetName: String?)
        fun onWeightChanged(weight: Double)
        fun submit(input: BookingInput)
    }

    /**
     * Immutable snapshot of the service displayed in the header card.
     * Keeps the View dumb — it just renders what the Presenter pushes.
     */
    data class Service(
        val id: String?,
        val name: String,
        val description: String,
        val pricePerKg: Double,
        val minDeliveryDays: Int,
        val iconRes: Int
    ) {
        companion object {
            fun fromBackend(response: ServiceResponse, preset: PresetService?): Service = Service(
                id = response.id,
                name = response.name,
                description = response.description ?: preset?.description ?: "Professional laundry care",
                pricePerKg = response.price,
                minDeliveryDays = preset?.minDeliveryDays ?: 1,
                iconRes = preset?.iconRes ?: 0
            )
        }
    }

    data class BookingInput(
        val weightKg: Double,
        val pickupDateIso: String?,
        val pickupSlotId: String?,
        val address: String,
        val deliveryDateIso: String?,
        val deliverySlotId: String?,
        val specialInstructions: String
    )
}
