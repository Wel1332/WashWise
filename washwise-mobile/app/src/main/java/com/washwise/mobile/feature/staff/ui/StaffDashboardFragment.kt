package com.washwise.mobile.feature.staff.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.washwise.mobile.R
import com.washwise.mobile.databinding.FragmentStaffDashboardBinding
import com.washwise.mobile.databinding.IncludeStaffStatCardBinding
import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.feature.staff.presenter.StaffDashboardContract
import com.washwise.mobile.feature.staff.presenter.StaffDashboardContract.Filter
import com.washwise.mobile.feature.staff.presenter.StaffDashboardContract.Stats
import com.washwise.mobile.feature.staff.presenter.StaffDashboardPresenter
import com.washwise.mobile.shared.util.SharedPrefManager

/**
 * View role for the Staff Dashboard. Pure presentation — delegates everything to
 * [StaffDashboardPresenter].
 */
class StaffDashboardFragment : Fragment(), StaffDashboardContract.View {

    private var _binding: FragmentStaffDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: StaffOrderAdapter
    private val presenter: StaffDashboardContract.Presenter = StaffDashboardPresenter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderGreeting()
        setupOrderList()
        setupStatCards()
        setupFilterChips()
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

    private fun renderGreeting() {
        val firstName = SharedPrefManager.getUserName()
            ?.trim()
            ?.split(" ")
            ?.firstOrNull()
            ?.takeIf { it.isNotBlank() }
            ?: "Staff"
        binding.tvStaffName.text = "Hi, $firstName 👋"
    }

    private fun setupOrderList() {
        orderAdapter = StaffOrderAdapter(onAdvance = { presenter.advance(it) })
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = orderAdapter
    }

    private fun setupStatCards() {
        configureStatCard(
            binding.cardTotal,
            iconRes = R.drawable.ic_box, iconTint = "#2563EB", iconBg = "#DBEAFE",
            label = "TOTAL", caption = "All orders"
        )
        configureStatCard(
            binding.cardActive,
            iconRes = R.drawable.ic_clock, iconTint = "#D97706", iconBg = "#FEF3C7",
            label = "ACTIVE", caption = "In progress"
        )
        configureStatCard(
            binding.cardDone,
            iconRes = R.drawable.ic_check_circle, iconTint = "#16A34A", iconBg = "#DCFCE7",
            label = "DONE", caption = "Completed"
        )
        configureStatCard(
            binding.cardPending,
            iconRes = R.drawable.ic_trending, iconTint = "#9810FA", iconBg = "#F3E8FF",
            label = "PENDING", caption = "Awaiting action"
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

    private fun setupFilterChips() {
        binding.llFilterChips.removeAllViews()
        Filter.values().forEachIndexed { index, filter ->
            val chip = TextView(requireContext()).apply {
                text = filter.label()
                textSize = 13f
                isClickable = true
                isFocusable = true
                isSelected = filter == Filter.ALL
                setBackgroundResource(R.drawable.bg_filter_chip)
                setTextColor(resources.getColorStateList(R.color.filter_chip_text, null))
                setPadding(dp(14), dp(8), dp(14), dp(8))
                setOnClickListener {
                    presenter.setFilter(filter)
                    for (i in 0 until binding.llFilterChips.childCount) {
                        binding.llFilterChips.getChildAt(i).isSelected = i == index
                    }
                }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(dp(4)) }
            chip.layoutParams = params
            binding.llFilterChips.addView(chip)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    // region View contract
    override fun renderStats(stats: Stats) {
        binding.cardTotal.tvStatValue.text = stats.total.toString()
        binding.cardActive.tvStatValue.text = stats.active.toString()
        binding.cardDone.tvStatValue.text = stats.completed.toString()
        binding.cardPending.tvStatValue.text = stats.pending.toString()
    }

    override fun renderFilterCounts(counts: Map<Filter, Int>) {
        for (i in 0 until binding.llFilterChips.childCount) {
            val view = binding.llFilterChips.getChildAt(i) as? TextView ?: continue
            val filter = Filter.values()[i]
            view.text = filter.labelWithCount(counts[filter] ?: 0)
        }
    }

    override fun renderOrders(orders: List<OrderResponse>) {
        binding.tvOrderCount.text = "${orders.size} ${if (orders.size == 1) "order" else "orders"}"
        if (orders.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvOrders.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvOrders.visibility = View.VISIBLE
            orderAdapter.submitList(orders)
        }
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun showStatusUpdated(orderId: String, newStatus: String) {
        Toast.makeText(
            requireContext(),
            "Marked as ${newStatus.lowercase().replaceFirstChar { it.uppercase() }}",
            Toast.LENGTH_SHORT
        ).show()
    }
    // endregion

    private fun Filter.label(): String = when (this) {
        Filter.ALL -> "All"
        Filter.PENDING -> "Pending"
        Filter.RECEIVED -> "Received"
        Filter.WASHING -> "Washing"
        Filter.DRYING -> "Drying"
        Filter.READY -> "Ready"
        Filter.COMPLETED -> "Completed"
        Filter.CANCELLED -> "Cancelled"
    }

    private fun Filter.labelWithCount(count: Int): String =
        if (this == Filter.ALL) "All ($count)" else "${label()} ($count)"
}
