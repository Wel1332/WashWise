package com.washwise.mobile.feature.staff.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.washwise.mobile.R
import com.washwise.mobile.databinding.ItemStaffOrderBinding
import com.washwise.mobile.feature.order.data.OrderResponse
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * RecyclerView adapter for the staff queue. Renders each order with status pill
 * and either an "Advance to <next>" action button or a terminal label.
 */
class StaffOrderAdapter(
    private val onAdvance: (OrderResponse) -> Unit
) : ListAdapter<OrderResponse, StaffOrderAdapter.StaffOrderViewHolder>(OrderDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffOrderViewHolder {
        val binding = ItemStaffOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StaffOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StaffOrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StaffOrderViewHolder(
        private val binding: ItemStaffOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderResponse) {
            binding.tvOrderId.text = formatOrderId(order.id)
            binding.tvCustomerName.text = order.user?.fullName ?: "Unknown"
            binding.tvCustomerEmail.text = order.user?.email ?: "—"
            binding.tvService.text = order.service?.name ?: "—"
            binding.tvWeight.text = order.weightKg
                ?.takeIf { it > 0.0 }
                ?.let { "${formatWeight(it)} kg" } ?: "—"
            binding.tvPickup.text = formatPickup(order.scheduledDate)

            applyStatusPill(order.status)
            applyActionRow(order)
        }

        private fun applyStatusPill(status: String?) {
            val style = StatusStyle.forStatus(status)
            binding.tvStatus.text = style.label
            binding.tvStatus.setBackgroundResource(style.pillBg)
            binding.tvStatus.setTextColor(Color.parseColor(style.textColor))
        }

        private fun applyActionRow(order: OrderResponse) {
            val status = (order.status ?: "").uppercase()
            val nextStatus = NEXT_STATUS[status]
            val terminal = status == "COMPLETED" || status == "CANCELLED"

            if (terminal) {
                binding.btnAdvance.visibility = View.GONE
                binding.llTerminal.visibility = View.VISIBLE
                if (status == "COMPLETED") {
                    binding.tvTerminalLabel.text = "Done"
                    binding.tvTerminalLabel.setTextColor(Color.parseColor("#16A34A"))
                    binding.ivTerminalIcon.setImageResource(R.drawable.ic_check_circle)
                } else {
                    binding.tvTerminalLabel.text = "Cancelled"
                    binding.tvTerminalLabel.setTextColor(Color.parseColor("#EF4444"))
                    binding.ivTerminalIcon.setImageResource(R.drawable.ic_check_circle)
                }
                binding.btnAdvance.setOnClickListener(null)
                return
            }

            if (nextStatus == null) {
                binding.btnAdvance.visibility = View.GONE
                binding.llTerminal.visibility = View.GONE
                return
            }

            binding.llTerminal.visibility = View.GONE
            binding.btnAdvance.visibility = View.VISIBLE
            binding.btnAdvance.text = "Advance to ${prettyStatus(nextStatus)}"
            binding.btnAdvance.setBackgroundColor(Color.parseColor(nextActionColor(nextStatus)))
            binding.btnAdvance.setOnClickListener { onAdvance(order) }
        }

        private fun formatOrderId(id: String): String =
            "WW-2026-${id.replace("-", "").take(6).uppercase()}"

        private fun formatWeight(value: Double): String =
            if (value % 1.0 == 0.0) value.toInt().toString()
            else String.format(Locale.US, "%.1f", value)

        private fun formatPickup(raw: String?): String {
            if (raw.isNullOrBlank()) return "—"
            return try {
                val isoIn = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                val out = SimpleDateFormat("MMM d, HH:mm", Locale.US)
                out.format(isoIn.parse(raw)!!)
            } catch (_: Exception) {
                raw
            }
        }
    }

    private data class StatusStyle(
        val label: String,
        val pillBg: Int,
        val textColor: String
    ) {
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

    companion object {
        private val NEXT_STATUS = mapOf(
            "PENDING" to "RECEIVED",
            "RECEIVED" to "WASHING",
            "WASHING" to "DRYING",
            "DRYING" to "READY",
            "READY" to "COMPLETED"
        )

        private fun prettyStatus(status: String): String =
            status.lowercase().replaceFirstChar { it.uppercase() }

        private fun nextActionColor(nextStatus: String): String = when (nextStatus) {
            "RECEIVED" -> "#9810FA"
            "WASHING" -> "#F97316"
            "DRYING" -> "#16A34A"
            "READY" -> "#2563EB"
            "COMPLETED" -> "#475569"
            else -> "#2563EB"
        }
    }
}
