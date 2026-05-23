package com.washwise.mobile.shared.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

/**
 * Decodes the `data:image/...;base64,XXX` data URL that the backend returns
 * for `profileImageBase64`. Returns null for null/blank input or malformed
 * payloads so callers can fall back to initials.
 */
object Base64Image {

    fun decode(dataUrl: String?): Bitmap? {
        if (dataUrl.isNullOrBlank()) return null
        val base64Part = dataUrl.substringAfter("base64,", missingDelimiterValue = dataUrl)
        return runCatching {
            val bytes = Base64.decode(base64Part, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }
}
