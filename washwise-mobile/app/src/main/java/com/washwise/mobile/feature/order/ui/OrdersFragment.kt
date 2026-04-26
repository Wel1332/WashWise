package com.washwise.mobile.feature.order.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.washwise.mobile.databinding.FragmentOrdersBinding
import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.feature.order.presenter.OrdersContract
import com.washwise.mobile.feature.order.presenter.OrdersPresenter

/**
 * View role for the Orders history screen. Delegates to [OrdersPresenter].
 */
class OrdersFragment : Fragment(), OrdersContract.View {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var activeAdapter: OrderAdapter
    private lateinit var completedAdapter: OrderAdapter
    private val presenter: OrdersContract.Presenter = OrdersPresenter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        binding.btnRetry.setOnClickListener { presenter.load() }
        presenter.attach(this)
        presenter.load()
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

    private fun setupRecyclerViews() {
        val onOrderClick = { order: OrderResponse ->
            startActivity(
                Intent(requireContext(), OrderTrackingActivity::class.java)
                    .putExtra(OrderTrackingActivity.EXTRA_ORDER_ID, order.id)
            )
        }
        activeAdapter = OrderAdapter(onOrderClick)
        binding.rvActiveOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActiveOrders.adapter = activeAdapter

        completedAdapter = OrderAdapter(onOrderClick)
        binding.rvCompletedOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCompletedOrders.adapter = completedAdapter
    }

    // region View contract
    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentState.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.GONE
    }

    override fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.contentState.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
        binding.errorState.visibility = View.GONE
    }

    override fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.contentState.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.VISIBLE
        binding.tvError.text = message
    }

    override fun showOrders(active: List<OrderResponse>, completed: List<OrderResponse>) {
        binding.progressBar.visibility = View.GONE
        binding.contentState.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.GONE

        binding.tvActiveOrdersHeader.text = "ACTIVE ORDERS (${active.size})"
        binding.tvCompletedOrdersHeader.text = "COMPLETED (${completed.size})"

        binding.llActiveSection.visibility = if (active.isNotEmpty()) View.VISIBLE else View.GONE
        binding.llCompletedSection.visibility = if (completed.isNotEmpty()) View.VISIBLE else View.GONE

        activeAdapter.submitList(active)
        completedAdapter.submitList(completed)
    }
    // endregion
}
