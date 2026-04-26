package com.washwise.mobile.feature.admin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.washwise.mobile.R
import com.washwise.mobile.databinding.ActivityAdminMainBinding
import com.washwise.mobile.feature.profile.ui.ProfileFragment

/**
 * App shell for admin users. Hosts Overview / Services / Users tabs plus the
 * shared Profile fragment behind a 4-tab bottom nav.
 */
class AdminMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(AdminOverviewFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_overview -> replaceFragment(AdminOverviewFragment())
                R.id.nav_admin_services -> replaceFragment(AdminServicesFragment())
                R.id.nav_admin_users -> replaceFragment(AdminUsersFragment())
                R.id.nav_admin_profile -> replaceFragment(ProfileFragment())
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
