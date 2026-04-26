package com.washwise.mobile.feature.profile.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.washwise.mobile.databinding.FragmentProfileBinding
import com.washwise.mobile.feature.auth.ui.LoginActivity
import com.washwise.mobile.feature.profile.data.UserResponse
import com.washwise.mobile.feature.profile.presenter.ProfileContract
import com.washwise.mobile.feature.profile.presenter.ProfilePresenter
import com.washwise.mobile.shared.util.SharedPrefManager

/**
 * View role for the Profile tab. Delegates to [ProfilePresenter].
 */
class ProfileFragment : Fragment(), ProfileContract.View {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val presenter: ProfileContract.Presenter = ProfilePresenter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindListeners()
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

    private fun bindListeners() {
        binding.rowEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), UpdateProfileActivity::class.java))
        }
        binding.rowAddresses.setOnClickListener { toast("Saved addresses coming soon") }
        binding.rowPayments.setOnClickListener { toast("Payment methods coming soon") }
        binding.rowNotifications.setOnClickListener { toast("Notification settings coming soon") }
        binding.rowSupport.setOnClickListener {
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Help & Support")
                .setMessage("Need help? Email us at support@washwise.com and we'll get back within 24 hours.")
                .setPositiveButton("Got it", null)
                .show()
        }
        binding.rowSignOut.setOnClickListener { confirmSignOut() }
    }

    private fun confirmSignOut() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Sign out?")
            .setMessage("You'll need to sign in again to access your orders.")
            .setPositiveButton("Sign out") { _, _ ->
                SharedPrefManager.clear()
                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // region View contract
    override fun renderProfile(profile: UserResponse, initials: String) {
        binding.tvName.text = profile.fullName.ifBlank { "User" }
        binding.tvEmail.text = profile.email
        binding.tvAvatarInitials.text = initials
        binding.tvAddressSubtitle.text = profile.address
            ?.takeIf { it.isNotBlank() }
            ?: "Add a pickup address"
    }

    override fun renderStats(orders: Int, completed: Int, rating: String) {
        binding.tvStatOrders.text = orders.toString()
        binding.tvStatCompleted.text = completed.toString()
        binding.tvStatRating.text = rating
    }
    // endregion
}
