package com.washwise.mobile.feature.dashboard.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.washwise.mobile.R
import com.washwise.mobile.databinding.FragmentDashboardBinding
import com.washwise.mobile.feature.dashboard.presenter.DashboardContract
import com.washwise.mobile.feature.dashboard.presenter.DashboardPresenter
import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.feature.order.ui.BookServiceActivity

/**
 * View role for the Dashboard. Renders what [DashboardPresenter] pushes and
 * forwards clicks. No network or business logic.
 */
class DashboardFragment : Fragment(), DashboardContract.View {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var serviceAdapter: ServiceAdapter
    private val presenter: DashboardContract.Presenter = DashboardPresenter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupServiceList()
        bindClicks()
        presenter.attach(this)
        presenter.start()
    }

    override fun onDestroyView() {
        presenter.detach()
        _binding = null
        super.onDestroyView()
    }

    private fun setupServiceList() {
        serviceAdapter = ServiceAdapter { service -> openBookService(service) }
        binding.rvServices.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvServices.adapter = serviceAdapter
    }

    private fun bindClicks() {
        val openBook = View.OnClickListener { openBookService(null) }
        binding.btnQuickBook.setOnClickListener(openBook)
        binding.flPromo.setOnClickListener(openBook)
        binding.offerCard1.setOnClickListener(openBook)
        binding.offerCard2.setOnClickListener(openBook)
        binding.offerCard3.setOnClickListener(openBook)
    }

    private fun openBookService(service: DashboardService?) {
        val intent = Intent(requireContext(), BookServiceActivity::class.java)
        service?.let {
            it.backendId?.let { id -> intent.putExtra(EXTRA_SERVICE_ID, id) }
            intent.putExtra(EXTRA_SERVICE_NAME, it.name)
        }
        startActivity(intent)
    }

    // region View contract
    override fun renderGreeting(timeOfDay: String, firstName: String) {
        binding.tvGreeting.text = timeOfDay
        binding.tvUserName.text = getString(R.string.dashboard_greeting, firstName)
    }

    override fun renderServices(services: List<DashboardService>) {
        serviceAdapter.submitList(services)
        binding.rvServices.visibility = View.VISIBLE
        binding.tvEmptyState.visibility = View.GONE
    }

    override fun renderActiveOrder(order: OrderResponse?) {
        if (order == null) {
            binding.llActiveOrder.visibility = View.GONE
            binding.tvNoActiveOrder.visibility = View.GONE
            return
        }
        binding.llActiveOrder.visibility = View.VISIBLE
        binding.tvNoActiveOrder.visibility = View.GONE
        binding.tvActiveOrderId.text = "WW-${order.id.take(8).uppercase()}"
        binding.tvActiveOrderService.text = order.service?.name ?: "Laundry Order"
        binding.tvActiveOrderStatus.text = (order.status ?: "Pending")
            .lowercase()
            .replaceFirstChar { it.uppercase() }
    }
    // endregion

    companion object {
        const val EXTRA_SERVICE_ID = "SERVICE_ID"
        const val EXTRA_SERVICE_NAME = "SERVICE_NAME"
    }
}
