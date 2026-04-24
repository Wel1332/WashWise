package com.washwise.mobile.feature.dashboard.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.washwise.mobile.R
import com.washwise.mobile.databinding.ItemServiceBinding

data class DashboardService(
    val backendId: String?,
    val name: String,
    val caption: String,
    val iconRes: Int,
    val iconTint: String,
    val bgTint: String
)

class ServiceAdapter(private val onServiceClick: (DashboardService) -> Unit) :
    ListAdapter<DashboardService, ServiceAdapter.ServiceViewHolder>(ServiceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ServiceViewHolder(private val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(service: DashboardService) {
            binding.tvServiceName.text = service.name
            binding.tvServicePrice.text = service.caption
            binding.ivServiceIcon.setImageResource(service.iconRes)
            binding.ivServiceIcon.setColorFilter(Color.parseColor(service.iconTint))
            binding.flIconContainer.background.setTint(Color.parseColor(service.bgTint))
            binding.root.setOnClickListener { onServiceClick(service) }
        }
    }

    class ServiceDiffCallback : DiffUtil.ItemCallback<DashboardService>() {
        override fun areItemsTheSame(oldItem: DashboardService, newItem: DashboardService): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: DashboardService, newItem: DashboardService): Boolean =
            oldItem == newItem
    }
}
