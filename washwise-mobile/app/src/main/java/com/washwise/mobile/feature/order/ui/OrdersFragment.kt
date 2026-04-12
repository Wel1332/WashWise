package com.washwise.mobile.feature.order.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.washwise.mobile.databinding.FragmentOrdersBinding
import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.shared.api.RetrofitClient
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var activeAdapter: OrderAdapter
    private lateinit var completedAdapter: OrderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        binding.btnRetry.setOnClickListener { fetchOrders() }
        fetchOrders()
    }

    private fun setupRecyclerViews() {
        val onOrderClick = { order: OrderResponse ->
            // For now, simple toast. Could launch OrderTrackingActivity
            Toast.makeText(requireContext(), "Order Tracking: ${order.id}", Toast.LENGTH_SHORT).show()
        }
        
        activeAdapter = OrderAdapter(onOrderClick)
        binding.rvActiveOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActiveOrders.adapter = activeAdapter

        completedAdapter = OrderAdapter(onOrderClick)
        binding.rvCompletedOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCompletedOrders.adapter = completedAdapter
    }

    private fun fetchOrders() {
        showLoading()
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getMyOrders()
                if (response.isSuccessful && response.body()?.success == true) {
                    val orders = response.body()?.data ?: emptyList()
                    if (orders.isEmpty()) {
                        showEmpty()
                    } else {
                        showOrders(orders)
                    }
                } else {
                    showError("Failed to load active orders")
                }
            } catch (e: Exception) {
                showError(e.message ?: "Network error occurred")
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentState.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.GONE
    }

    private fun showOrders(orders: List<OrderResponse>) {
        binding.progressBar.visibility = View.GONE
        binding.contentState.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.GONE

        val activeOrders = orders.filter { it.status.uppercase() != "COMPLETED" && it.status.uppercase() != "CANCELLED" }
        val completedOrders = orders.filter { it.status.uppercase() == "COMPLETED" || it.status.uppercase() == "CANCELLED" }

        binding.tvActiveOrdersHeader.text = "ACTIVE ORDERS (${activeOrders.size})"
        binding.tvCompletedOrdersHeader.text = "COMPLETED (${completedOrders.size})"

        binding.llActiveSection.visibility = if (activeOrders.isNotEmpty()) View.VISIBLE else View.GONE
        binding.llCompletedSection.visibility = if (completedOrders.isNotEmpty()) View.VISIBLE else View.GONE

        activeAdapter.submitList(activeOrders)
        completedAdapter.submitList(completedOrders)
    }

    private fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.contentState.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
        binding.errorState.visibility = View.GONE
    }

    private fun showError(msg: String) {
        binding.progressBar.visibility = View.GONE
        binding.contentState.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.VISIBLE
        binding.tvError.text = msg
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
