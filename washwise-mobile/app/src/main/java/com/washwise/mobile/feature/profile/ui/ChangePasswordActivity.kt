package com.washwise.mobile.feature.profile.ui

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.washwise.mobile.databinding.ActivityChangePasswordBinding
import com.washwise.mobile.feature.profile.presenter.ChangePasswordContract
import com.washwise.mobile.feature.profile.presenter.ChangePasswordContract.ChangeInput
import com.washwise.mobile.feature.profile.presenter.ChangePasswordContract.Field
import com.washwise.mobile.feature.profile.presenter.ChangePasswordContract.StrengthLevel
import com.washwise.mobile.feature.profile.presenter.ChangePasswordPresenter

/**
 * View role for the Change Password screen. All business logic lives in
 * [ChangePasswordPresenter]; this class only renders state and forwards input.
 */
class ChangePasswordActivity : AppCompatActivity(), ChangePasswordContract.View {

    private lateinit var binding: ActivityChangePasswordBinding
    private val presenter: ChangePasswordContract.Presenter = ChangePasswordPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
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
        binding.btnChange.setOnClickListener {
            presenter.submit(
                ChangeInput(
                    current = binding.etCurrent.text.toString(),
                    newPassword = binding.etNew.text.toString(),
                    confirmPassword = binding.etConfirm.text.toString()
                )
            )
        }
        binding.etNew.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                presenter.onNewPasswordChanged(s?.toString().orEmpty())
            }
        })
    }

    // region View contract
    override fun showSubmitting() {
        binding.btnChange.isEnabled = false
        binding.progressSubmit.visibility = View.VISIBLE
    }

    override fun hideSubmitting() {
        binding.btnChange.isEnabled = true
        binding.progressSubmit.visibility = View.GONE
    }

    override fun showSuccess() {
        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showFieldError(field: Field, message: String) {
        val target = when (field) {
            Field.CURRENT -> binding.etCurrent
            Field.NEW -> binding.etNew
            Field.CONFIRM -> binding.etConfirm
        }
        target.error = message
        target.requestFocus()
    }

    override fun showStrength(level: StrengthLevel) {
        binding.tvStrength.apply {
            text = "Strength: ${level.label}"
            setTextColor(Color.parseColor(level.colorHex))
            visibility = View.VISIBLE
        }
    }

    override fun close() {
        finish()
    }
    // endregion
}
