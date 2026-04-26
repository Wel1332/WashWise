package com.washwise.mobile.feature.order.presenter

import com.washwise.mobile.feature.order.data.OrderResponse

/**
 * MVP contract for the Order Tracking screen.
 */
interface OrderTrackingContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun renderOrder(order: OrderResponse, currentStepIndex: Int, percentComplete: Int)
        fun showError(message: String)
        fun close()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun load(orderId: String)
    }

    /** Fixed list of tracking steps shared between Presenter and View. */
    enum class Step(val title: String, val description: String) {
        ORDER_PLACED("Order Placed", "Your order has been received"),
        PICKED_UP("Picked Up", "Driver collected your laundry"),
        IN_WASH("In the Wash", "Clothes are being washed"),
        DRYING("Drying", "Clothes are tumble-drying"),
        READY("Ready for Pickup", "Packed and ready"),
        DELIVERED("Delivered", "Enjoy your clean laundry!")
    }
}
