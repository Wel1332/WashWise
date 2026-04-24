package com.washwise.mobile.feature.dashboard.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.washwise.mobile.R
import com.washwise.mobile.databinding.FragmentDashboardBinding
import com.washwise.mobile.feature.order.ui.BookServiceActivity
import com.washwise.mobile.feature.service.data.ServiceResponse
import com.washwise.mobile.shared.api.RetrofitClient
import com.washwise.mobile.shared.util.SharedPrefManager
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var serviceAdapter: ServiceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGreeting()
        setupServiceList()
        setupClicks()
        renderServices(emptyList())
        fetchServices()
        fetchActiveOrder()
    }

    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
        binding.tvGreeting.text = greeting

        val fullName = SharedPrefManager.getUserName()?.trim().orEmpty()
        val firstName = fullName.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: "there"
        binding.tvUserName.text = "Hi, $firstName! 👋"
    }

    private fun setupServiceList() {
        serviceAdapter = ServiceAdapter { service ->
            val intent = Intent(requireContext(), BookServiceActivity::class.java).apply {
                service.backendId?.let { putExtra("SERVICE_ID", it) }
                putExtra("SERVICE_NAME", service.name)
            }
            startActivity(intent)
        }
        binding.rvServices.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvServices.adapter = serviceAdapter
    }

    private fun setupClicks() {
        val openBook = View.OnClickListener {
            startActivity(Intent(requireContext(), BookServiceActivity::class.java))
        }
        binding.btnQuickBook.setOnClickListener(openBook)
        binding.flPromo.setOnClickListener(openBook)
        binding.tvServicesViewAll.setOnClickListener(openBook)
        binding.tvOffersSeeAll.setOnClickListener(openBook)
        binding.offerCard1.setOnClickListener(openBook)
        binding.offerCard2.setOnClickListener(openBook)
        binding.offerCard3.setOnClickListener(openBook)
    }

    private fun renderServices(apiServices: List<ServiceResponse>) {
        fun findBackend(vararg aliases: String): ServiceResponse? {
            val lowered = aliases.map { it.lowercase() }.toSet()
            return apiServices.firstOrNull { it.name.lowercase() in lowered }
        }

        fun pricePerKg(s: ServiceResponse?, fallback: Double): String =
            String.format(Locale.US, "₱%.0f/kg", s?.price ?: fallback)

        val washOnly = findBackend("wash only")
        val washDryFold = findBackend("wash-dry-fold", "wash dry fold", "wash & fold")
        val dryClean = findBackend("dry cleaning", "dry clean")
        val premium = findBackend("premium care", "premium")

        val tiles = listOf(
            DashboardService(
                backendId = washOnly?.id,
                name = "Wash Only",
                caption = pricePerKg(washOnly, 30.0),
                iconRes = R.drawable.ic_droplet,
                iconTint = "#0891B2",
                bgTint = "#CFFAFE"
            ),
            DashboardService(
                backendId = washDryFold?.id,
                name = "Wash-Dry-Fold",
                caption = pricePerKg(washDryFold, 40.0),
                iconRes = R.drawable.ic_tshirt,
                iconTint = "#2563EB",
                bgTint = "#DBEAFE"
            ),
            DashboardService(
                backendId = dryClean?.id,
                name = "Dry Cleaning",
                caption = pricePerKg(dryClean, 150.0),
                iconRes = R.drawable.ic_sparkle,
                iconTint = "#9810FA",
                bgTint = "#F3E8FF"
            ),
            DashboardService(
                backendId = premium?.id,
                name = "Premium Care",
                caption = pricePerKg(premium, 175.0),
                iconRes = R.drawable.ic_star,
                iconTint = "#FF6B35",
                bgTint = "#FFEDD5"
            )
        )
        serviceAdapter.submitList(tiles)
        binding.rvServices.visibility = View.VISIBLE
        binding.tvEmptyState.visibility = View.GONE
    }

    private fun fetchServices() {
        binding.progressBar.visibility = View.GONE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getActiveServices()
                if (response.isSuccessful && response.body()?.success == true) {
                    renderServices(response.body()?.data ?: emptyList())
                }
            } catch (_: Exception) {
                // keep the preset tiles on network failure
            }
        }
    }

    private fun fetchActiveOrder() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getMyOrders()
                val orders = response.body()?.data.orEmpty()
                val active = orders.firstOrNull {
                    val status = it.status.uppercase()
                    status !in listOf("DELIVERED", "COMPLETED", "CANCELLED", "CANCELED")
                }
                if (active != null) {
                    binding.llActiveOrder.visibility = View.VISIBLE
                    binding.tvNoActiveOrder.visibility = View.GONE
                    binding.tvActiveOrderId.text = "WW-${active.id.take(8).uppercase()}"
                    binding.tvActiveOrderService.text = active.service?.name ?: "Laundry Order"
                    binding.tvActiveOrderStatus.text = active.status
                        .lowercase()
                        .replaceFirstChar { it.uppercase() }
                } else {
                    binding.llActiveOrder.visibility = View.GONE
                    binding.tvNoActiveOrder.visibility = View.GONE
                }
            } catch (_: Exception) {
                binding.llActiveOrder.visibility = View.GONE
                binding.tvNoActiveOrder.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
