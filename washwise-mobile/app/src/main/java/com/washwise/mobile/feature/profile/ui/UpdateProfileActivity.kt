package com.washwise.mobile.feature.profile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.washwise.mobile.databinding.ActivityUpdateProfileBinding
import com.washwise.mobile.feature.profile.data.UserResponse
import com.washwise.mobile.feature.profile.presenter.UpdateProfileContract
import com.washwise.mobile.feature.profile.presenter.UpdateProfileContract.Field
import com.washwise.mobile.feature.profile.presenter.UpdateProfileContract.UpdateInput
import com.washwise.mobile.feature.profile.presenter.UpdateProfilePresenter

/**
 * View role in the MVP triad. Delegates all business logic to [UpdateProfilePresenter].
 */
class UpdateProfileActivity : AppCompatActivity(), UpdateProfileContract.View {

    private lateinit var binding: ActivityUpdateProfileBinding
    private val presenter: UpdateProfileContract.Presenter = UpdateProfilePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.attach(this)
        bindClicks()
        presenter.loadProfile()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    private fun bindClicks() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { presenter.save(collectInput()) }
        binding.rowChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
    }

    private fun collectInput(): UpdateInput = UpdateInput(
        fullName = binding.etFullName.text.toString(),
        phoneNumber = binding.etPhone.text.toString(),
        bio = binding.etBio.text.toString(),
        address = binding.etAddress.text.toString(),
        city = binding.etCity.text.toString(),
        zipCode = binding.etZip.text.toString()
    )

    // region View contract
    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showSaving() {
        binding.btnSave.isEnabled = false
        binding.progressSave.visibility = View.VISIBLE
    }

    override fun hideSaving() {
        binding.btnSave.isEnabled = true
        binding.progressSave.visibility = View.GONE
    }

    override fun renderProfile(profile: UserResponse) {
        binding.tvHeaderName.text = profile.fullName.ifBlank { "Your Profile" }
        binding.tvHeaderEmail.text = profile.email
        binding.tvAvatarInitials.text = initialsOf(profile.fullName)

        binding.etFullName.setText(profile.fullName)
        binding.etPhone.setText(profile.phoneNumber.orEmpty())
        binding.etBio.setText(profile.bio.orEmpty())
        binding.etAddress.setText(profile.address.orEmpty())
        binding.etCity.setText(profile.city.orEmpty())
        binding.etZip.setText(profile.zipCode.orEmpty())
    }

    override fun showSaveSuccess() {
        Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showFieldError(field: Field, message: String) {
        val target = when (field) {
            Field.FULL_NAME -> binding.etFullName
            Field.PHONE -> binding.etPhone
            Field.ADDRESS -> binding.etAddress
            Field.CITY -> binding.etCity
            Field.ZIP -> binding.etZip
            Field.BIO -> binding.etBio
        }
        target.error = message
        target.requestFocus()
    }

    override fun close() {
        finish()
    }
    // endregion

    private fun initialsOf(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "U"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts[0].first()}${parts[1].first()}".uppercase()
        }
    }
}
