package com.washwise.mobile.feature.dashboard.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.washwise.mobile.shared.api.RetrofitClient
import com.washwise.mobile.databinding.FragmentDashboardBinding
import com.washwise.mobile.feature.order.ui.BookServiceActivity
import kotlinx.coroutines.launch
import com.washwise.mobile.feature.dashboard.ui.ServiceAdapter
import com.washwise.mobile.feature.service.data.ServiceResponse

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ServiceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchServices()
    }

    private fun setupRecyclerView() {
        adapter = ServiceAdapter { service ->
            val intent = Intent(requireContext(), BookServiceActivity::class.java).apply {
                // Pass service details if needed
                putExtra("SERVICE_ID", service.id)
                putExtra("SERVICE_NAME", service.name)
            }
            startActivity(intent)
        }
        binding.rvServices.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvServices.adapter = adapter
    }

    private fun fetchServices() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvServices.visibility = View.GONE
        binding.tvEmptyState.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getActiveServices()
                if (response.isSuccessful && response.body()?.success == true) {
                    val services = response.body()?.data ?: emptyList()
                    if (services.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                    } else {
                        binding.rvServices.visibility = View.VISIBLE
                        adapter.submitList(services)
                    }
                } else {
                    binding.tvEmptyState.text = "Failed to load services"
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.rvServices.visibility = View.GONE
                }
            } catch (e: Exception) {
                binding.tvEmptyState.text = e.message ?: "Network error occurred"
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvServices.visibility = View.GONE
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}