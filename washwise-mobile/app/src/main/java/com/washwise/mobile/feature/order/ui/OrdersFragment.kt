package com.washwise.mobile.feature.order.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.btnRetry.setOnClickListener { fetchOrders() }
        fetchOrders()
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter { order -> showCancelDialog(order) }
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter
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
                    showError("Failed to load orders")
                }
            } catch (e: Exception) {
                showError("Network error: ${e.localizedMessage}")
            }
        }
    }

    private fun showCancelDialog(order: OrderResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Order")
            .setMessage("Are you sure you want to cancel this order?")
            .setPositiveButton("Yes, Cancel") { _, _ -> cancelOrder(order) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelOrder(order: OrderResponse) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.cancelOrder(order.id)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Order cancelled", Toast.LENGTH_SHORT).show()
                    fetchOrders() // Refresh list
                } else {
                    Toast.makeText(context, "Failed to cancel order", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvOrders.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.GONE
    }

    private fun showOrders(orders: List<OrderResponse>) {
        binding.progressBar.visibility = View.GONE
        binding.rvOrders.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.GONE
        binding.tvOrderCount.text = "${orders.size} order(s)"
        adapter.submitList(orders)
    }

    private fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.rvOrders.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
        binding.errorState.visibility = View.GONE
        binding.tvOrderCount.text = "0 orders"
    }

    private fun showError(msg: String) {
        binding.progressBar.visibility = View.GONE
        binding.rvOrders.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.VISIBLE
        binding.tvError.text = msg
        binding.tvOrderCount.text = "Error"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
