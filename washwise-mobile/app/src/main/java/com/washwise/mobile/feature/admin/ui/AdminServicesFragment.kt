package com.washwise.mobile.feature.admin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.washwise.mobile.databinding.DialogCreateServiceBinding
import com.washwise.mobile.databinding.FragmentAdminServicesBinding
import com.washwise.mobile.feature.admin.data.CreateServiceRequest
import com.washwise.mobile.feature.admin.presenter.AdminServicesContract
import com.washwise.mobile.feature.admin.presenter.AdminServicesPresenter
import com.washwise.mobile.feature.service.data.ServiceResponse

/**
 * View role for the Admin Services tab. Shows the catalog and exposes a
 * BottomSheet form for creating a new service.
 */
class AdminServicesFragment : Fragment(), AdminServicesContract.View {

    private var _binding: FragmentAdminServicesBinding? = null
    private val binding get() = _binding!!
    private val presenter: AdminServicesContract.Presenter = AdminServicesPresenter()
    private lateinit var serviceAdapter: AdminServiceAdapter

    private var dialogBinding: DialogCreateServiceBinding? = null
    private var bottomSheet: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceAdapter = AdminServiceAdapter()
        binding.rvServices.layoutManager = LinearLayoutManager(requireContext())
        binding.rvServices.adapter = serviceAdapter
        binding.fabAddService.setOnClickListener { openCreateDialog() }
        presenter.attach(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.load()
    }

    override fun onDestroyView() {
        presenter.detach()
        bottomSheet?.dismiss()
        bottomSheet = null
        dialogBinding = null
        _binding = null
        super.onDestroyView()
    }

    private fun openCreateDialog() {
        val sheet = BottomSheetDialog(requireContext())
        val dialog = DialogCreateServiceBinding.inflate(layoutInflater)
        sheet.setContentView(dialog.root)

        dialog.spCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            CATEGORIES
        )

        dialog.btnCancel.setOnClickListener { sheet.dismiss() }
        dialog.btnSave.setOnClickListener {
            val request = CreateServiceRequest(
                name = dialog.etName.text.toString().trim(),
                description = dialog.etDescription.text.toString().trim(),
                price = dialog.etPrice.text.toString().toDoubleOrNull() ?: 0.0,
                category = dialog.spCategory.selectedItem?.toString() ?: "Wash",
                duration = dialog.etDuration.text.toString().trim().ifBlank { "24-48 hours" },
                isActive = true
            )
            presenter.createService(request)
        }

        bottomSheet = sheet
        dialogBinding = dialog
        sheet.setOnDismissListener {
            bottomSheet = null
            dialogBinding = null
        }
        sheet.show()
    }

    // region View contract
    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvServices.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun renderServices(services: List<ServiceResponse>) {
        if (services.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvServices.visibility = View.GONE
            return
        }
        binding.emptyState.visibility = View.GONE
        binding.rvServices.visibility = View.VISIBLE
        serviceAdapter.submitList(services)
    }

    override fun showCreating() {
        dialogBinding?.btnSave?.isEnabled = false
        dialogBinding?.progressSubmit?.visibility = View.VISIBLE
    }

    override fun hideCreating() {
        dialogBinding?.btnSave?.isEnabled = true
        dialogBinding?.progressSubmit?.visibility = View.GONE
    }

    override fun showCreateSuccess() {
        Toast.makeText(requireContext(), "Service created", Toast.LENGTH_SHORT).show()
        bottomSheet?.dismiss()
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    // endregion

    companion object {
        private val CATEGORIES = listOf("Wash", "Dry Clean", "Premium", "Ironing")
    }
}
