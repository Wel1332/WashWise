package com.washwise.mobile.feature.admin.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.washwise.mobile.databinding.FragmentAdminUsersBinding
import com.washwise.mobile.feature.admin.data.AdminUser
import com.washwise.mobile.feature.admin.presenter.AdminUsersContract
import com.washwise.mobile.feature.admin.presenter.AdminUsersPresenter

/**
 * View role for the Admin Users tab. Shows a list of users; tapping "Change Role"
 * opens an AlertDialog with the three role options.
 */
class AdminUsersFragment : Fragment(), AdminUsersContract.View {

    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!
    private val presenter: AdminUsersContract.Presenter = AdminUsersPresenter()
    private lateinit var userAdapter: AdminUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userAdapter = AdminUserAdapter(onChangeRole = ::askForNewRole)
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = userAdapter
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

    private fun askForNewRole(user: AdminUser) {
        val labels = ROLES.map { it.second }.toTypedArray()
        val current = ROLES.indexOfFirst { it.first.equals(user.role, ignoreCase = true) }
            .takeIf { it >= 0 } ?: 0
        AlertDialog.Builder(requireContext())
            .setTitle("Change role")
            .setSingleChoiceItems(labels, current) { dialog, which ->
                val newRole = ROLES[which].first
                if (!newRole.equals(user.role, ignoreCase = true)) {
                    presenter.changeRole(user.id, newRole)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // region View contract
    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvUsers.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun renderUsers(users: List<AdminUser>, currentUserId: String?) {
        if (users.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvUsers.visibility = View.GONE
            return
        }
        binding.emptyState.visibility = View.GONE
        binding.rvUsers.visibility = View.VISIBLE
        userAdapter.submitList(users.map {
            AdminUserAdapter.Item(
                user = it,
                isCurrentUser = it.id == currentUserId
            )
        })
    }

    override fun showRoleUpdated(user: AdminUser) {
        Toast.makeText(
            requireContext(),
            "${user.fullName ?: "User"} is now ${user.role}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    // endregion

    companion object {
        private val ROLES = listOf(
            "CUSTOMER" to "Customer",
            "STAFF" to "Staff",
            "ADMIN" to "Admin"
        )
    }
}
