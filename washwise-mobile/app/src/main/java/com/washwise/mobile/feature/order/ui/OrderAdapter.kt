package com.washwise.mobile.feature.order.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.washwise.mobile.databinding.ItemOrderBinding
import com.washwise.mobile.feature.order.data.OrderResponse

class OrderAdapter(
    private val onCancelClick: (OrderResponse) -> Unit
) : ListAdapter<OrderResponse, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderResponse) {
            binding.tvServiceName.text = order.service?.name ?: "Unknown Service"
            binding.tvLocation.text = order.location ?: "—"
            binding.tvDate.text = formatDate(order.scheduledDate)
            binding.tvPrice.text = "₱${String.format("%.2f", order.totalPrice)}"

            // Status badge
            binding.tvStatus.text = order.status.uppercase()
            val (bgColor, textColor) = getStatusColors(order.status)
            val badgeBg = binding.tvStatus.background as? GradientDrawable
                ?: GradientDrawable().apply {
                    cornerRadius = 40f
                    binding.tvStatus.background = this
                }
            badgeBg.setColor(bgColor)
            binding.tvStatus.setTextColor(textColor)

            // Show cancel button only for PENDING orders
            if (order.status.equals("PENDING", ignoreCase = true)) {
                binding.btnCancel.visibility = View.VISIBLE
                binding.btnCancel.setOnClickListener { onCancelClick(order) }
            } else {
                binding.btnCancel.visibility = View.GONE
            }
        }

        private fun formatDate(dateStr: String?): String {
            if (dateStr.isNullOrEmpty()) return "—"
            // Take just the date part if it's a datetime string
            return dateStr.take(10)
        }

        private fun getStatusColors(status: String): Pair<Int, Int> {
            return when (status.uppercase()) {
                "PENDING" -> Pair(Color.parseColor("#FFF3E0"), Color.parseColor("#E65100"))
                "CONFIRMED" -> Pair(Color.parseColor("#E3F2FD"), Color.parseColor("#1565C0"))
                "IN_PROGRESS", "PROCESSING" -> Pair(Color.parseColor("#E8F5E9"), Color.parseColor("#2E7D32"))
                "COMPLETED" -> Pair(Color.parseColor("#E8F5E9"), Color.parseColor("#1B5E20"))
                "CANCELLED" -> Pair(Color.parseColor("#FFEBEE"), Color.parseColor("#C62828"))
                "PICKED_UP" -> Pair(Color.parseColor("#F3E5F5"), Color.parseColor("#6A1B9A"))
                else -> Pair(Color.parseColor("#F5F5F5"), Color.parseColor("#616161"))
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
