package com.washwise.mobile.feature.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.washwise.mobile.databinding.ActivityLoginBinding
import com.washwise.mobile.feature.auth.presenter.LoginContract
import com.washwise.mobile.feature.auth.presenter.LoginContract.Field
import com.washwise.mobile.feature.auth.presenter.LoginContract.Role
import com.washwise.mobile.feature.auth.presenter.LoginPresenter
import com.washwise.mobile.feature.admin.ui.AdminMainActivity
import com.washwise.mobile.feature.staff.ui.StaffMainActivity
import com.washwise.mobile.ui.main.MainActivity

/**
 * View role of the Login MVP triad. Only renders state and forwards input to
 * the [LoginPresenter]. Holds no business logic or network calls.
 */
class LoginActivity : AppCompatActivity(), LoginContract.View {

    private lateinit var binding: ActivityLoginBinding
    private val presenter: LoginContract.Presenter = LoginPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.attach(this)
        bindListeners()
        presenter.start()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    private fun bindListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnLogin.setOnClickListener {
            presenter.submit(
                email = binding.etEmail.text.toString(),
                password = binding.etPassword.text.toString()
            )
        }
        binding.tvRegister.setOnClickListener { presenter.onRegisterClicked() }
    }

    // region View contract
    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
    }

    override fun showFieldError(field: Field, message: String) {
        val target = when (field) {
            Field.EMAIL -> binding.etEmail
            Field.PASSWORD -> binding.etPassword
        }
        target.error = message
        target.requestFocus()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToHome(role: Role) {
        val target = when (role) {
            Role.ADMIN -> AdminMainActivity::class.java
            Role.STAFF -> StaffMainActivity::class.java
            Role.CUSTOMER -> MainActivity::class.java
        }
        startActivity(Intent(this, target))
        finish()
    }

    override fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
    // endregion
}
