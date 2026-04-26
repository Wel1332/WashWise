package com.washwise.mobile.feature.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.washwise.mobile.databinding.ActivityRegisterBinding
import com.washwise.mobile.feature.auth.presenter.RegisterContract
import com.washwise.mobile.feature.auth.presenter.RegisterContract.Field
import com.washwise.mobile.feature.auth.presenter.RegisterContract.RegisterInput
import com.washwise.mobile.feature.auth.presenter.RegisterPresenter
import com.washwise.mobile.ui.main.MainActivity

/**
 * View role for the Register screen. Delegates to [RegisterPresenter].
 */
class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var binding: ActivityRegisterBinding
    private val presenter: RegisterContract.Presenter = RegisterPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.attach(this)
        bindListeners()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    private fun bindListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.tvLogin.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener {
            presenter.submit(
                RegisterInput(
                    fullName = binding.etFullName.text.toString(),
                    email = binding.etEmail.text.toString(),
                    password = binding.etPassword.text.toString(),
                    confirmPassword = binding.etConfirmPassword.text.toString()
                )
            )
        }
    }

    // region View contract
    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnRegister.isEnabled = true
    }

    override fun showFieldError(field: Field, message: String) {
        val target = when (field) {
            Field.FULL_NAME -> binding.etFullName
            Field.EMAIL -> binding.etEmail
            Field.PASSWORD -> binding.etPassword
            Field.CONFIRM_PASSWORD -> binding.etConfirmPassword
        }
        target.error = message
        target.requestFocus()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToHome() {
        Toast.makeText(this, "Welcome to WashWise!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
    // endregion
}
