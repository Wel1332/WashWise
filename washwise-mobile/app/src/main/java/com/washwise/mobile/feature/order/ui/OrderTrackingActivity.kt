package com.washwise.mobile.feature.order.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.washwise.mobile.R
import com.washwise.mobile.databinding.ActivityOrderTrackingBinding
import com.washwise.mobile.databinding.ItemTimelineStepBinding
import com.washwise.mobile.feature.order.data.OrderResponse
import com.washwise.mobile.feature.order.presenter.OrderTrackingContract
import com.washwise.mobile.feature.order.presenter.OrderTrackingContract.Step
import com.washwise.mobile.feature.order.presenter.OrderTrackingPresenter
import java.util.Locale

/**
 * View role for the Order Tracking screen. Delegates data fetching + state mapping
 * to [OrderTrackingPresenter].
 */
class OrderTrackingActivity : AppCompatActivity(), OrderTrackingContract.View {

    private lateinit var binding: ActivityOrderTrackingBinding
    private val presenter: OrderTrackingContract.Presenter = OrderTrackingPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        val orderId = intent.getStringExtra(EXTRA_ORDER_ID)
        if (orderId.isNullOrBlank()) {
            Toast.makeText(this, "Missing order id", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        presenter.attach(this)
        presenter.load(orderId)
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    // region View contract
    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun renderOrder(order: OrderResponse, currentStepIndex: Int, percentComplete: Int) {
        val shortId = order.id.replace("-", "").take(6).uppercase()
        binding.tvHeaderOrderId.text = "WW-2026-$shortId"

        val status = order.status ?: "PENDING"
        binding.tvHeaderStatus.text = prettyStatus(status)
        applyStatusPill(status)

        binding.tvTrackService.text = order.service?.name ?: "Laundry Service"

        val weight = order.weightKg?.takeIf { it > 0.0 } ?: extractWeight(order.notes)
        binding.tvTrackWeight.text = weight?.let { formatWeight(it) + " kg" } ?: "—"

        binding.tvTrackTotal.text = String.format(Locale.US, "₱%.0f", order.totalPrice ?: 0.0)
        binding.tvTrackPickup.text = order.scheduledDate?.let(::formatPickup) ?: "—"

        binding.tvProgressPercent.text = "$percentComplete% complete"
        binding.vProgressFill.post {
            val parent = binding.vProgressFill.parent as? View ?: return@post
            val params = binding.vProgressFill.layoutParams
            params.width = (parent.width * percentComplete / 100f).toInt()
            binding.vProgressFill.layoutParams = params
        }

        buildSteps(currentStepIndex)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun close() {
        finish()
    }
    // endregion

    private fun buildSteps(currentIndex: Int) {
        binding.llSteps.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val steps = Step.values()
        steps.forEachIndexed { index, step ->
            val row = ItemTimelineStepBinding.inflate(inflater, binding.llSteps, false)

            row.tvStepTitle.text = step.title
            row.tvStepDesc.text = step.description

            if (index == steps.size - 1) {
                row.vConnector.visibility = View.INVISIBLE
            }

            val iconRes = iconFor(step)
            when {
                index < currentIndex -> styleCompleted(row)
                index == currentIndex -> styleCurrent(row, iconRes)
                else -> stylePending(row, iconRes)
            }

            binding.llSteps.addView(row.root)
        }
    }

    private fun styleCompleted(row: ItemTimelineStepBinding) {
        row.flDot.setBackgroundResource(R.drawable.bg_circle_blue)
        row.ivDotIcon.setImageResource(R.drawable.ic_check)
        row.ivDotIcon.setColorFilter(Color.WHITE)
        row.tvStepTitle.setTextColor(Color.parseColor("#111827"))
        row.tvStepDesc.setTextColor(Color.parseColor("#6B7280"))
        row.tvStepBadge.visibility = View.GONE
    }

    private fun styleCurrent(row: ItemTimelineStepBinding, iconRes: Int) {
        row.flDot.setBackgroundResource(R.drawable.bg_circle_blue)
        row.ivDotIcon.setImageResource(iconRes)
        row.ivDotIcon.setColorFilter(Color.WHITE)
        row.tvStepTitle.setTextColor(Color.parseColor("#111827"))
        row.tvStepDesc.setTextColor(Color.parseColor("#6B7280"))
        row.tvStepBadge.visibility = View.VISIBLE
    }

    private fun stylePending(row: ItemTimelineStepBinding, iconRes: Int) {
        row.flDot.setBackgroundResource(R.drawable.bg_circle_grey_soft)
        row.ivDotIcon.setImageResource(iconRes)
        row.ivDotIcon.setColorFilter(Color.parseColor("#9CA3AF"))
        row.tvStepTitle.setTextColor(Color.parseColor("#9CA3AF"))
        row.tvStepDesc.setTextColor(Color.parseColor("#D1D5DB"))
        row.tvStepBadge.visibility = View.GONE
    }

    private fun iconFor(step: Step): Int = when (step) {
        Step.ORDER_PLACED -> R.drawable.ic_clock
        Step.PICKED_UP -> R.drawable.ic_box
        Step.IN_WASH -> R.drawable.ic_droplet
        Step.DRYING -> R.drawable.ic_wind
        Step.READY -> R.drawable.ic_check_circle
        Step.DELIVERED -> R.drawable.ic_package
    }

    private fun applyStatusPill(status: String) {
        val (bgRes, textColor) = when (status.uppercase()) {
            "PENDING" -> R.drawable.bg_pill_amber to "#B45309"
            "PICKED_UP", "PICKED-UP",
            "READY", "READY_FOR_PICKUP", "READY-FOR-PICKUP" -> R.drawable.bg_badge_blue to "#1D4ED8"
            "WASHING", "IN_PROGRESS", "IN-PROGRESS", "PROCESSING", "DRYING" ->
                R.drawable.bg_pill_purple to "#7E22CE"
            "DELIVERED", "COMPLETED" -> R.drawable.bg_pill_grey_soft to "#475569"
            "CANCELLED", "CANCELED" -> R.drawable.bg_pill_grey_soft to "#EF4444"
            else -> R.drawable.bg_pill_grey_soft to "#475569"
        }
        binding.tvHeaderStatus.setBackgroundResource(bgRes)
        binding.tvHeaderStatus.setTextColor(Color.parseColor(textColor))
    }

    private fun prettyStatus(status: String): String = when (status.uppercase()) {
        "PENDING" -> "Pending"
        "PICKED_UP", "PICKED-UP" -> "Picked Up"
        "WASHING", "IN_PROGRESS", "IN-PROGRESS", "PROCESSING" -> "Washing"
        "DRYING" -> "Drying"
        "READY", "READY_FOR_PICKUP", "READY-FOR-PICKUP" -> "Ready"
        "DELIVERED", "COMPLETED" -> "Completed"
        "CANCELLED", "CANCELED" -> "Cancelled"
        else -> status.lowercase().replaceFirstChar { it.uppercase() }
    }

    private fun extractWeight(notes: String?): Double? {
        if (notes.isNullOrBlank()) return null
        val match = Regex("""Weight[^0-9]*([0-9]+(?:\.[0-9]+)?)""", RegexOption.IGNORE_CASE)
            .find(notes)
        return match?.groupValues?.getOrNull(1)?.toDoubleOrNull()
    }

    private fun formatWeight(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString()
        else String.format(Locale.US, "%.1f", value)

    private fun formatPickup(raw: String): String {
        val datePart = raw.substringBefore('T')
        val timePart = raw.substringAfter('T', "")
        val hhmm = timePart.take(5)
        return if (hhmm.isNotEmpty()) "$datePart at $hhmm" else datePart
    }

    companion object {
        const val EXTRA_ORDER_ID = "ORDER_ID"
    }
}
