package com.washwise.mobile.feature.order.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.washwise.mobile.databinding.ItemOrderBinding
import com.washwise.mobile.feature.order.data.OrderResponse

class OrderAdapter(
    private val onOrderClick: (OrderResponse) -> Unit
) : ListAdapter<OrderResponse, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderResponse) {
            // Setup Order ID (mocked format WW-2026-XX like mockup)
            val mockId = order.id.takeLast(2).padStart(2, '0').uppercase()
            binding.tvOrderId.text = "WW-2026-$mockId"
            
            // Subtitle
            val serviceName = order.service?.name ?: "Service"
            // For mockup matching, just displaying a generic weight if not provided
            binding.tvServiceSub.text = "$serviceName · 5 kg"
            
            binding.tvPrice.text = "$${String.format("%.0f", order.totalPrice)}"

            // Status badge styling
            val status = order.status.lowercase().replaceFirstChar { it.uppercase() }
            binding.tvStatus.text = status
            
            val (bgColor, textColor) = getStatusColors(order.status)
            val badgeBg = binding.tvStatus.background as? GradientDrawable
                ?: GradientDrawable().apply {
                    cornerRadius = 40f
                    binding.tvStatus.background = this
                }
            badgeBg.setColor(bgColor)
            binding.tvStatus.setTextColor(textColor)
            
            // Set dynamic colors based on service name
            val iconTint: String
            val iconBg: String
            when (serviceName.lowercase()) {
                "wash & fold" -> { iconTint = "#2B7CFF"; iconBg = "#EAF2FF" }
                "dry clean" -> { iconTint = "#A855F7"; iconBg = "#F3E8FF" }
                "ironing" -> { iconTint = "#F97316"; iconBg = "#FFEDD5" }
                "premium care" -> { iconTint = "#22C55E"; iconBg = "#DCFCE7" }
                "express wash" -> { iconTint = "#14B8A6"; iconBg = "#CCFBF1" }
                "delivery" -> { iconTint = "#EC4899"; iconBg = "#FCE7F3" }
                else -> { iconTint = "#6C757D"; iconBg = "#F8F9FA" }
            }
            binding.ivServiceIcon.setColorFilter(Color.parseColor(iconTint))
            binding.flIconContainer.background.setTint(Color.parseColor(iconBg))

            binding.root.setOnClickListener { onOrderClick(order) }
        }

        private fun getStatusColors(status: String): Pair<Int, Int> {
            return when (status.uppercase()) {
                "PENDING" -> Pair(Color.parseColor("#FFF8E1"), Color.parseColor("#F59E0B"))
                "WASHING", "IN_PROGRESS", "PROCESSING" -> Pair(Color.parseColor("#F3E8FF"), Color.parseColor("#A855F7"))
                "COMPLETED" -> Pair(Color.parseColor("#F1F5F9"), Color.parseColor("#475569"))
                "CANCELLED" -> Pair(Color.parseColor("#FEF2F2"), Color.parseColor("#EF4444"))
                "PICKED_UP" -> Pair(Color.parseColor("#EFF6FF"), Color.parseColor("#3B82F6"))
                else -> Pair(Color.parseColor("#F1F5F9"), Color.parseColor("#475569"))
            }
        }
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
}
