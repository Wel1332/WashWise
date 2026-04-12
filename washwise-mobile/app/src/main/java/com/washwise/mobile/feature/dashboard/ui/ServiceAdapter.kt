package com.washwise.mobile.feature.dashboard.ui

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.washwise.mobile.databinding.ItemServiceBinding
import com.washwise.mobile.feature.service.data.ServiceResponse
import java.util.Locale

class ServiceAdapter(private val onServiceClick: (ServiceResponse) -> Unit) :
    ListAdapter<ServiceResponse, ServiceAdapter.ServiceViewHolder>(ServiceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ServiceViewHolder(private val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service: ServiceResponse) {
            binding.tvServiceName.text = service.name
            binding.tvServiceDesc.text = service.description ?: "High quality service"
            binding.tvServicePrice.text = String.format(Locale.US, "$%.0f/kg", service.price)
            
            // Set dynamic colors based on service name as per UI
            val iconTint: String
            val bgTint: String
            when (service.name.lowercase()) {
                "wash & fold" -> {
                    iconTint = "#2B7CFF"
                    bgTint = "#EAF2FF"
                }
                "dry clean" -> {
                    iconTint = "#A855F7"
                    bgTint = "#F3E8FF"
                }
                "ironing" -> {
                    iconTint = "#F97316"
                    bgTint = "#FFEDD5"
                }
                "premium care" -> {
                    iconTint = "#22C55E"
                    bgTint = "#DCFCE7"
                }
                "express wash" -> {
                    iconTint = "#14B8A6"
                    bgTint = "#CCFBF1"
                }
                "delivery" -> {
                    iconTint = "#EC4899"
                    bgTint = "#FCE7F3"
                }
                else -> {
                    iconTint = "#2B7CFF"
                    bgTint = "#EAF2FF"
                }
            }
            binding.ivServiceIcon.setColorFilter(Color.parseColor(iconTint))
            // We use a Drawable background for flIconContainer, so we tint it
            binding.flIconContainer.background.setTint(Color.parseColor(bgTint))
            binding.tvServicePrice.setTextColor(Color.parseColor(iconTint))

            binding.root.setOnClickListener {
                onServiceClick(service)
            }
        }
    }

    class ServiceDiffCallback : DiffUtil.ItemCallback<ServiceResponse>() {
        override fun areItemsTheSame(oldItem: ServiceResponse, newItem: ServiceResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ServiceResponse, newItem: ServiceResponse): Boolean {
            return oldItem == newItem
        }
    }
}
