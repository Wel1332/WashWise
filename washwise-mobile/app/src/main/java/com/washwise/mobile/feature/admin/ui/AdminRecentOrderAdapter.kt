package com.washwise.mobile.feature.admin.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.washwise.mobile.R
import com.washwise.mobile.databinding.ItemAdminRecentOrderBinding
import com.washwise.mobile.feature.order.data.OrderResponse
import java.util.Locale

/**
 * Compact recent-order row for the Admin Overview tab.
 */
class AdminRecentOrderAdapter :
    ListAdapter<OrderResponse, AdminRecentOrderAdapter.RecentOrderViewHolder>(OrderDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentOrderViewHolder {
        val binding = ItemAdminRecentOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecentOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentOrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecentOrderViewHolder(
        private val binding: ItemAdminRecentOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderResponse) {
            binding.tvOrderId.text = "WW-2026-${order.id.replace("-", "").take(6).uppercase()}"
            binding.tvCustomerService.text = listOfNotNull(
                order.user?.fullName,
                order.service?.name
            ).joinToString(" · ").ifBlank { "—" }

            val style = StatusStyle.forStatus(order.status)
            binding.tvStatus.text = style.label
            binding.tvStatus.setBackgroundResource(style.pillBgRes)
            binding.tvStatus.setTextColor(Color.parseColor(style.textColor))

            binding.tvTotal.text = String.format(Locale.US, "₱%.0f", order.totalPrice ?: 0.0)
        }
    }

    private data class StatusStyle(val label: String, val pillBgRes: Int, val textColor: String) {
        companion object {
            fun forStatus(status: String?): StatusStyle = when ((status ?: "").uppercase()) {
                "PENDING" -> StatusStyle("Pending", R.drawable.bg_pill_amber, "#B45309")
                "RECEIVED" -> StatusStyle("Received", R.drawable.bg_badge_blue, "#1D4ED8")
                "WASHING" -> StatusStyle("Washing", R.drawable.bg_pill_purple, "#7E22CE")
                "DRYING" -> StatusStyle("Drying", R.drawable.bg_pill_purple, "#7E22CE")
                "READY" -> StatusStyle("Ready", R.drawable.bg_badge_blue, "#1D4ED8")
                "DELIVERED", "COMPLETED" -> StatusStyle("Completed", R.drawable.bg_pill_grey_soft, "#475569")
                "CANCELLED", "CANCELED" -> StatusStyle("Cancelled", R.drawable.bg_pill_grey_soft, "#EF4444")
                else -> StatusStyle(
                    (status ?: "Pending").lowercase().replaceFirstChar { it.uppercase() },
                    R.drawable.bg_pill_grey_soft,
                    "#475569"
                )
            }
        }
    }

    class OrderDiff : DiffUtil.ItemCallback<OrderResponse>() {
        override fun areItemsTheSame(oldItem: OrderResponse, newItem: OrderResponse): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: OrderResponse, newItem: OrderResponse): Boolean =
            oldItem == newItem
    }
}
