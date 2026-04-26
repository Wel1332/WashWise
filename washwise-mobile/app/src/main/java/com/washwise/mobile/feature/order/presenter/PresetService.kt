package com.washwise.mobile.feature.order.presenter

import com.washwise.mobile.R

/**
 * UI-side catalog of supported services. Mirrors the web app so price, min turnaround
 * and icon stay consistent when the backend hasn't yet been populated.
 */
data class PresetService(
    val name: String,
    val description: String,
    val pricePerKg: Double,
    val minDeliveryDays: Int,
    val iconRes: Int
) {
    companion object {
        private val CATALOG = listOf(
            PresetService("Wash Only", "Basic washing for everyday items", 30.0, 1, R.drawable.ic_droplet),
            PresetService("Wash-Dry-Fold", "Complete everyday laundry care", 40.0, 2, R.drawable.ic_tshirt),
            PresetService("Dry Cleaning", "Professional care for delicates", 150.0, 3, R.drawable.ic_sparkle),
            PresetService("Premium Care", "Special handling for luxury items", 175.0, 5, R.drawable.ic_star)
        )

        fun fromName(name: String?): PresetService? {
            if (name.isNullOrBlank()) return null
            val normalized = name.normalize()
            return CATALOG.firstOrNull { it.name.normalize() == normalized }
                ?: CATALOG.firstOrNull { it.name.normalize().contains(normalized) }
                ?: CATALOG.firstOrNull { normalized.contains(it.name.normalize()) }
        }

        private fun String.normalize(): String =
            lowercase().replace(" ", "").replace("-", "")
    }
}
