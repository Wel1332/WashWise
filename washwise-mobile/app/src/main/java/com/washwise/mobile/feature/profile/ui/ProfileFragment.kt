package com.washwise.mobile.feature.profile.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.washwise.mobile.shared.api.RetrofitClient
import com.washwise.mobile.databinding.FragmentProfileBinding
import com.washwise.mobile.feature.auth.ui.LoginActivity
import com.washwise.mobile.shared.util.SharedPrefManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        fetchProfile() // Refresh data when returning from edit screen
    }

    private fun fetchProfile() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getProfile()
                if (response.isSuccessful && response.body()?.success == true) {
                    val profile = response.body()?.data
                    binding.tvName.text = profile?.fullName ?: "N/A"
                    binding.tvEmail.text = profile?.email ?: "N/A"
                    
                    val phoneStr = profile?.phoneNumber
                    binding.tvPhone.text = if (phoneStr.isNullOrEmpty()) "Not set" else phoneStr
                    
                    val addressStr = profile?.address
                    binding.tvAddress.text = if (addressStr.isNullOrEmpty()) "Not set" else addressStr
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), UpdateProfileActivity::class.java))
        }
        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
        }
        
        binding.btnDownloadData?.setOnClickListener {
            Toast.makeText(context, "A copy of your data will be sent to your email later.", Toast.LENGTH_LONG).show()
        }

        binding.btnPrivacySettings?.setOnClickListener {
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Privacy Settings")
                .setMessage("Your profile is completely private and only shared with verified washing partners fulfilling your orders. You can opt out of promotional emails by contacting support.")
                .setPositiveButton("Got it", null)
                .show()
        }

        binding.btnDeleteAccount?.setOnClickListener {
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account? This action cannot be undone and all your order history will be removed.")
                .setPositiveButton("Delete") { _, _ ->
                    deleteAccount()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        
        binding.btnLogout.setOnClickListener {
            SharedPrefManager.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun deleteAccount() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.deleteAccount()
                if (response.isSuccessful) {
                    Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                    binding.btnLogout.performClick()
                } else {
                    Toast.makeText(context, "Failed to delete account", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
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