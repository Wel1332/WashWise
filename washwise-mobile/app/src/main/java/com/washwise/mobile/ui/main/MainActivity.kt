package com.washwise.mobile.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.washwise.mobile.R
import com.washwise.mobile.databinding.ActivityMainBinding
import com.washwise.mobile.feature.dashboard.ui.DashboardFragment
import com.washwise.mobile.feature.order.ui.BookServiceActivity
import com.washwise.mobile.feature.order.ui.OrdersFragment
import com.washwise.mobile.feature.profile.ui.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load dashboard by default
        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> replaceFragment(DashboardFragment())
                R.id.nav_orders -> replaceFragment(OrdersFragment())
                R.id.nav_book -> {
                    startActivity(Intent(this, BookServiceActivity::class.java))
                    return@setOnItemSelectedListener false // Don't select this tab
                }
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}