package com.washwise.mobile.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.washwise.mobile.data.api.RetrofitClient
import com.washwise.mobile.databinding.FragmentDashboardBinding
import com.washwise.mobile.utils.SharedPrefManager
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = SharedPrefManager.getUserName() ?: "User"
        binding.tvWelcome.text = "Welcome back,\n$userName!"

        fetchOrders()
    }

    private fun fetchOrders() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvTotalOrders.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getMyOrders()
                if (response.isSuccessful && response.body()?.success == true) {
                    val orders = response.body()?.data ?: emptyList()
                    binding.tvTotalOrders.text = orders.size.toString()
                } else {
                    Toast.makeText(context, "Failed to load orders", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.tvTotalOrders.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}