package com.washwise.mobile.feature.staff.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.washwise.mobile.R
import com.washwise.mobile.databinding.ActivityStaffMainBinding
import com.washwise.mobile.feature.profile.ui.ProfileFragment

/**
 * App shell for staff users. Hosts the queue dashboard and the shared Profile
 * fragment behind a 2-tab bottom nav.
 */
class StaffMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(StaffDashboardFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_staff_dashboard -> replaceFragment(StaffDashboardFragment())
                R.id.nav_staff_profile -> replaceFragment(ProfileFragment())
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
