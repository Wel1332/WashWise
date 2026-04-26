package com.washwise.mobile.feature.order.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.washwise.mobile.R
import com.washwise.mobile.databinding.ItemOrderBinding
import com.washwise.mobile.feature.order.data.OrderResponse
import java.util.Locale

class OrderAdapter(
    private val onOrderClick: (OrderResponse) -> Unit
) : ListAdapter<OrderResponse, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderResponse) {
            val shortId = order.id.replace("-", "").take(6).uppercase()
            binding.tvOrderId.text = "WW-2026-$shortId"

            val serviceName = order.service?.name ?: "Laundry Service"
            val weight = order.weightKg?.takeIf { it > 0.0 } ?: extractWeight(order.notes)
            val weightText = weight?.let { " · ${formatWeight(it)} kg" } ?: ""
            binding.tvServiceSub.text = "$serviceName$weightText"

            binding.tvPrice.text = String.format(Locale.US, "₱%.0f", order.totalPrice ?: 0.0)

            val styled = StatusStyle.forStatus(order.status)
            binding.tvStatus.text = styled.label
            binding.tvStatus.setBackgroundResource(styled.pillBgRes)
            binding.tvStatus.setTextColor(Color.parseColor(styled.textColor))

            val iconStyle = ServiceIconStyle.forName(serviceName)
            binding.ivServiceIcon.setImageResource(iconStyle.iconRes)
            binding.ivServiceIcon.setColorFilter(Color.parseColor(iconStyle.iconTint))
            binding.flIconContainer.background.setTint(Color.parseColor(iconStyle.bgTint))

            binding.root.setOnClickListener { onOrderClick(order) }
        }

        private fun extractWeight(notes: String?): Double? {
            if (notes.isNullOrBlank()) return null
            val match = Regex("""Weight[^0-9]*([0-9]+(?:\.[0-9]+)?)""", RegexOption.IGNORE_CASE)
                .find(notes)
            return match?.groupValues?.getOrNull(1)?.toDoubleOrNull()
        }

        private fun formatWeight(value: Double): String =
            if (value % 1.0 == 0.0) value.toInt().toString()
            else String.format(Locale.US, "%.1f", value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<OrderResponse>() {
        override fun areItemsTheSame(oldItem: OrderResponse, newItem: OrderResponse) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: OrderResponse, newItem: OrderResponse) =
            oldItem == newItem
    }

    private data class StatusStyle(
        val label: String,
        val pillBgRes: Int,
        val textColor: String
    ) {
        companion object {
            fun forStatus(status: String?): StatusStyle = when ((status ?: "").uppercase()) {
                "PENDING" -> StatusStyle("Pending", R.drawable.bg_pill_amber, "#B45309")
                "PICKED_UP", "PICKED-UP" ->
                    StatusStyle("Picked Up", R.drawable.bg_badge_blue, "#1D4ED8")
                "WASHING", "IN_PROGRESS", "IN-PROGRESS", "PROCESSING" ->
                    StatusStyle("Washing", R.drawable.bg_pill_purple, "#7E22CE")
                "DRYING" -> StatusStyle("Drying", R.drawable.bg_pill_purple, "#7E22CE")
                "READY", "READY_FOR_PICKUP", "READY-FOR-PICKUP" ->
                    StatusStyle("Ready", R.drawable.bg_badge_blue, "#1D4ED8")
                "DELIVERED", "COMPLETED" ->
                    StatusStyle("Completed", R.drawable.bg_pill_grey_soft, "#475569")
                "CANCELLED", "CANCELED" ->
                    StatusStyle("Cancelled", R.drawable.bg_pill_grey_soft, "#EF4444")
                else -> StatusStyle(
                    (status ?: "Pending").lowercase().replaceFirstChar { it.uppercase() },
                    R.drawable.bg_pill_grey_soft,
                    "#475569"
                )
            }
        }
    }

    private data class ServiceIconStyle(
        val iconRes: Int,
        val iconTint: String,
        val bgTint: String
    ) {
        companion object {
            fun forName(name: String): ServiceIconStyle {
                val n = name.lowercase()
                return when {
                    "wash only" in n -> ServiceIconStyle(R.drawable.ic_droplet, "#0891B2", "#CFFAFE")
                    "wash-dry-fold" in n || "wash & fold" in n || "wash dry fold" in n ->
                        ServiceIconStyle(R.drawable.ic_tshirt, "#2563EB", "#DBEAFE")
                    "dry clean" in n || "dry cleaning" in n ->
                        ServiceIconStyle(R.drawable.ic_sparkle, "#9810FA", "#F3E8FF")
                    "premium" in n ->
                        ServiceIconStyle(R.drawable.ic_star, "#FF6B35", "#FFEDD5")
                    "iron" in n ->
                        ServiceIconStyle(R.drawable.ic_iron, "#14B8A6", "#CCFBF1")
                    else -> ServiceIconStyle(R.drawable.ic_box, "#2563EB", "#DBEAFE")
                }
            }
        }
    }
}
