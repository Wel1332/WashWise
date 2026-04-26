package com.washwise.mobile.feature.admin.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.washwise.mobile.databinding.ItemAdminServiceBinding
import com.washwise.mobile.feature.service.data.ServiceResponse
import java.util.Locale

/**
 * Renders a service row in the Admin Services tab.
 */
class AdminServiceAdapter :
    ListAdapter<ServiceResponse, AdminServiceAdapter.ServiceViewHolder>(ServiceDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemAdminServiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ServiceViewHolder(
        private val binding: ItemAdminServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(service: ServiceResponse) {
            binding.tvServiceName.text = service.name
            binding.tvCategory.text = service.category ?: "Uncategorized"
            binding.tvServiceDescription.text = service.description ?: "No description"
            binding.tvServicePrice.text =
                String.format(Locale.US, "₱%.0f/kg", service.price)
            binding.tvServiceDuration.text = service.duration ?: "—"
        }
    }

    class ServiceDiff : DiffUtil.ItemCallback<ServiceResponse>() {
        override fun areItemsTheSame(oldItem: ServiceResponse, newItem: ServiceResponse): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ServiceResponse, newItem: ServiceResponse): Boolean =
            oldItem == newItem
    }
}
