package com.washwise.mobile.feature.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.washwise.mobile.databinding.ActivityLandingBinding
import com.washwise.mobile.shared.util.SharedPrefManager

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Smart Feature: If they are already logged in, skip the welcome screen!
        if (SharedPrefManager.isLoggedIn()) {
            startActivity(Intent(this, com.washwise.mobile.ui.main.MainActivity::class.java))
            finish()
            return
        }

        // ==========================================
        // --- ANIMATIONS ---
        // ==========================================
        binding.topSection.alpha = 0f
        binding.topSection.translationY = -80f

        binding.cardBottom.alpha = 0f
        binding.cardBottom.translationY = 150f
        binding.topSection.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(DecelerateInterpolator())
            .start()
        binding.cardBottom.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setStartDelay(300) // Waits 0.3 seconds before starting
            .setInterpolator(DecelerateInterpolator())
            .start()

        // ==========================================
        // --- BUTTON CLICKS ---
        // ==========================================

        binding.btnNavigateToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnNavigateToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}