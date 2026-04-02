package com.washwise.mobile.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.washwise.mobile.data.api.RetrofitClient
import com.washwise.mobile.data.model.request.RegisterRequest
import com.washwise.mobile.databinding.ActivityRegisterBinding
import com.washwise.mobile.ui.main.MainActivity
import com.washwise.mobile.utils.SharedPrefManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInput(fullName, email, password, confirmPassword)) {
                performRegistration(fullName, email, password, confirmPassword)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish() // Closes RegisterActivity and returns to LoginActivity
        }
    }

    private fun validateInput(fullName: String, email: String, pass: String, confirmPass: String): Boolean {
        if (fullName.isEmpty()) {
            binding.etFullName.error = "Name is required"
            return false
        }
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }
        if (pass.length < 8) {
            binding.etPassword.error = "Password must be at least 8 characters"
            return false
        }
        if (pass != confirmPass) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return false
        }
        return true
    }

    private fun performRegistration(fullName: String, email: String, pass: String, confirmPass: String) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val request = RegisterRequest(email, pass, confirmPass, fullName)
                val response = RetrofitClient.instance.register(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!.data!!

                    // Save session data using the exact fields from our new AuthResponse
                    SharedPrefManager.saveAuthSession(
                        token = data.accessToken,
                        refreshToken = data.refreshToken,
                        id = data.id,
                        name = data.fullName,
                        email = data.email,
                        role = data.role
                    )

                    Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finishAffinity() // Clears the back stack so they can't hit back to register again
                } else {
                    val message = response.body()?.message ?: "Registration failed"
                    Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Network error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }
}