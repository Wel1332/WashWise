package com.washwise.mobile.feature.admin.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.washwise.mobile.R
import com.washwise.mobile.databinding.FragmentAdminOverviewBinding
import com.washwise.mobile.databinding.IncludeStaffStatCardBinding
import com.washwise.mobile.feature.admin.presenter.AdminOverviewContract
import com.washwise.mobile.feature.admin.presenter.AdminOverviewContract.Stats
import com.washwise.mobile.feature.admin.presenter.AdminOverviewContract.StatusBucket
import com.washwise.mobile.feature.admin.presenter.AdminOverviewPresenter
import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.shared.util.SharedPrefManager
import java.util.Locale

/**
 * View role for the Admin Overview tab. Renders stats / breakdown / recent
 * orders pushed by [AdminOverviewPresenter].
 */
class AdminOverviewFragment : Fragment(), AdminOverviewContract.View {

    private var _binding: FragmentAdminOverviewBinding? = null
    private val binding get() = _binding!!
    private val presenter: AdminOverviewContract.Presenter = AdminOverviewPresenter()
    private lateinit var breakdownAdapter: AdminBreakdownAdapter
    private lateinit var recentAdapter: AdminRecentOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderHeader()
        setupStatCards()
        setupBreakdown()
        setupRecentOrders()
        presenter.attach(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.load()
    }

    override fun onDestroyView() {
        presenter.detach()
        _binding = null
        super.onDestroyView()
    }

    private fun renderHeader() {
        val firstName = SharedPrefManager.getUserName()
            ?.trim()
            ?.split(" ")
            ?.firstOrNull()
            ?.takeIf { it.isNotBlank() }
            ?: "Admin"
        binding.tvAdminName.text = "Welcome back, $firstName"
    }

    private fun setupStatCards() {
        configureStatCard(
            binding.cardTotal,
            R.drawable.ic_box, "#2563EB", "#DBEAFE",
            "TOTAL ORDERS", "All time"
        )
        configureStatCard(
            binding.cardActive,
            R.drawable.ic_clock, "#D97706", "#FEF3C7",
            "ACTIVE", "In progress"
        )
        configureStatCard(
            binding.cardRevenue,
            R.drawable.ic_trending, "#16A34A", "#DCFCE7",
            "REVENUE", "Total earned"
        )
        configureStatCard(
            binding.cardUsers,
            R.drawable.ic_profile, "#9810FA", "#F3E8FF",
            "USERS", "Registered"
        )
    }

    private fun configureStatCard(
        card: IncludeStaffStatCardBinding,
        iconRes: Int,
        iconTint: String,
        iconBg: String,
        label: String,
        caption: String
    ) {
        card.ivStatIcon.setImageResource(iconRes)
        card.ivStatIcon.setColorFilter(Color.parseColor(iconTint))
        card.flStatIconContainer.background.setTint(Color.parseColor(iconBg))
        card.tvStatLabel.text = label
        card.tvStatCaption.text = caption
        card.tvStatValue.text = "0"
    }

    private fun setupBreakdown() {
        breakdownAdapter = AdminBreakdownAdapter()
        binding.rvBreakdown.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBreakdown.adapter = breakdownAdapter
    }

    private fun setupRecentOrders() {
        recentAdapter = AdminRecentOrderAdapter()
        binding.rvRecentOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentOrders.adapter = recentAdapter
    }

    // region View contract
    override fun renderStats(stats: Stats) {
        binding.cardTotal.tvStatValue.text = stats.totalOrders.toString()
        binding.cardActive.tvStatValue.text = stats.activeOrders.toString()
        binding.cardRevenue.tvStatValue.text = String.format(Locale.US, "₱%.0f", stats.revenue)
        binding.cardUsers.tvStatValue.text = stats.users.toString()
    }

    override fun renderBreakdown(buckets: List<StatusBucket>) {
        breakdownAdapter.submitList(buckets)
    }

    override fun renderRecentOrders(orders: List<OrderResponse>) {
        if (orders.isEmpty()) {
            binding.tvRecentEmpty.visibility = View.VISIBLE
            binding.rvRecentOrders.visibility = View.GONE
        } else {
            binding.tvRecentEmpty.visibility = View.GONE
            binding.rvRecentOrders.visibility = View.VISIBLE
            recentAdapter.submitList(orders)
        }
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    // endregion
}
