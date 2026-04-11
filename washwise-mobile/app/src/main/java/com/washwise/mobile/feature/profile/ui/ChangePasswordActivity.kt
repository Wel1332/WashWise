package com.washwise.mobile.feature.profile.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.washwise.mobile.shared.api.RetrofitClient
import com.washwise.mobile.feature.profile.data.ChangePasswordRequest
import com.washwise.mobile.databinding.ActivityChangePasswordBinding
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChange.setOnClickListener {
            val current = binding.etCurrent.text.toString().trim()
            val newPass = binding.etNew.text.toString().trim()

            if(current.isEmpty() || newPass.isEmpty()) return@setOnClickListener

            binding.btnChange.isEnabled = false

            lifecycleScope.launch {
                try {
                    val request = ChangePasswordRequest(current, newPass)
                    val response = RetrofitClient.instance.changePassword(request)

                    if (response.isSuccessful) {
                        Toast.makeText(this@ChangePasswordActivity, "Password Updated", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ChangePasswordActivity, "Failed to update password", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ChangePasswordActivity, "Network Error", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.btnChange.isEnabled = true
                }
            }
        }
    }
}