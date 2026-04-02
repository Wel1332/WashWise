package com.washwise.mobile.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.washwise.mobile.data.api.RetrofitClient
import com.washwise.mobile.data.model.request.UpdateProfileRequest
import com.washwise.mobile.databinding.ActivityUpdateProfileBinding
import kotlinx.coroutines.launch

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val city = binding.etCity.text.toString().trim()

            // Show loading state here ideally
            binding.btnSave.isEnabled = false

            lifecycleScope.launch {
                try {
                    val request = UpdateProfileRequest(null, null, phone, address, city, null)
                    val response = RetrofitClient.instance.updateProfile(request)

                    if (response.isSuccessful) {
                        Toast.makeText(this@UpdateProfileActivity, "Profile Updated", Toast.LENGTH_SHORT).show()
                        finish() // Go back to profile screen
                    } else {
                        Toast.makeText(this@UpdateProfileActivity, "Update Failed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@UpdateProfileActivity, "Network Error", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.btnSave.isEnabled = true
                }
            }
        }
    }
}