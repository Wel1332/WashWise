package com.washwise.mobile.feature.profile.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.washwise.mobile.databinding.ActivityUpdateProfileBinding
import com.washwise.mobile.feature.profile.data.UserResponse
import com.washwise.mobile.feature.profile.presenter.UpdateProfileContract
import com.washwise.mobile.feature.profile.presenter.UpdateProfileContract.Field
import com.washwise.mobile.feature.profile.presenter.UpdateProfileContract.UpdateInput
import com.washwise.mobile.feature.profile.presenter.UpdateProfilePresenter
import com.washwise.mobile.shared.util.Base64Image

/**
 * View role in the MVP triad. Delegates all business logic to [UpdateProfilePresenter].
 */
class UpdateProfileActivity : AppCompatActivity(), UpdateProfileContract.View {

    private lateinit var binding: ActivityUpdateProfileBinding
    private val presenter: UpdateProfileContract.Presenter = UpdateProfilePresenter()

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) handlePickedImage(uri)
    }

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
        binding.avatarContainer.setOnClickListener {
            pickImage.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    private fun handlePickedImage(uri: Uri) {
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val bytes = runCatching {
            contentResolver.openInputStream(uri)?.use { it.readBytes() }
        }.getOrNull()
        if (bytes == null) {
            Toast.makeText(this, "Couldn't read the selected image", Toast.LENGTH_LONG).show()
            return
        }
        val extension = mimeType.substringAfter("/", "jpg")
        val filename = "profile_${System.currentTimeMillis()}.$extension"
        presenter.uploadImage(bytes, mimeType, filename)
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

    override fun showUploadingImage() {
        binding.avatarContainer.isEnabled = false
        binding.progressAvatar.visibility = View.VISIBLE
    }

    override fun hideUploadingImage() {
        binding.avatarContainer.isEnabled = true
        binding.progressAvatar.visibility = View.GONE
    }

    override fun renderProfile(profile: UserResponse) {
        binding.tvHeaderName.text = profile.fullName.ifBlank { "Your Profile" }
        binding.tvHeaderEmail.text = profile.email
        binding.tvAvatarInitials.text = initialsOf(profile.fullName)

        val bitmap = Base64Image.decode(profile.profileImageBase64)
        if (bitmap != null) {
            binding.ivAvatar.setImageBitmap(bitmap)
            binding.ivAvatar.visibility = View.VISIBLE
            binding.tvAvatarInitials.visibility = View.GONE
        } else {
            binding.ivAvatar.visibility = View.GONE
            binding.tvAvatarInitials.visibility = View.VISIBLE
        }

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

    override fun showImageUploadSuccess() {
        Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show()
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
