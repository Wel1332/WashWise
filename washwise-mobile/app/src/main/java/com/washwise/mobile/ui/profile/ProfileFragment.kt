package com.washwise.mobile.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.washwise.mobile.data.api.RetrofitClient
import com.washwise.mobile.databinding.FragmentProfileBinding
import com.washwise.mobile.ui.auth.LoginActivity
import com.washwise.mobile.utils.SharedPrefManager
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
                    binding.tvPhone.text = "Phone: ${profile?.phoneNumber ?: "Not set"}"
                    binding.tvAddress.text = "Address: ${profile?.address ?: "Not set"}"
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
        binding.btnLogout.setOnClickListener {
            SharedPrefManager.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}