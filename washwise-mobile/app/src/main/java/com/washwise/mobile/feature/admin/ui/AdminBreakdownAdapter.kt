package com.washwise.mobile.feature.admin.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.washwise.mobile.databinding.ItemAdminStatusBreakdownBinding
import com.washwise.mobile.feature.admin.presenter.AdminOverviewContract.StatusBucket

/**
 * Renders a single status breakdown row (label + count + horizontal progress bar).
 */
class AdminBreakdownAdapter :
    ListAdapter<StatusBucket, AdminBreakdownAdapter.BreakdownViewHolder>(BreakdownDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreakdownViewHolder {
        val binding = ItemAdminStatusBreakdownBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BreakdownViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BreakdownViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BreakdownViewHolder(
        private val binding: ItemAdminStatusBreakdownBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bucket: StatusBucket) {
            binding.tvBreakdownLabel.text = bucket.label
            binding.tvBreakdownCount.text = bucket.count.toString()
            binding.vBreakdownFill.post {
                val parent = binding.vBreakdownFill.parent as? android.view.View ?: return@post
                val params = binding.vBreakdownFill.layoutParams
                params.width = (parent.width * bucket.percent / 100f).toInt()
                binding.vBreakdownFill.layoutParams = params
            }
        }
    }

    class BreakdownDiff : DiffUtil.ItemCallback<StatusBucket>() {
        override fun areItemsTheSame(oldItem: StatusBucket, newItem: StatusBucket): Boolean =
            oldItem.status == newItem.status

        override fun areContentsTheSame(oldItem: StatusBucket, newItem: StatusBucket): Boolean =
            oldItem == newItem
    }
}
