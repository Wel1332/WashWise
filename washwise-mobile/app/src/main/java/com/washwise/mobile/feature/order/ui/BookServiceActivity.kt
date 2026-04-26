package com.washwise.mobile.feature.order.ui

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.washwise.mobile.R
import com.washwise.mobile.databinding.ActivityBookServiceBinding
import com.washwise.mobile.feature.dashboard.ui.DashboardFragment
import com.washwise.mobile.feature.order.presenter.BookServiceContract
import com.washwise.mobile.feature.order.presenter.BookServiceContract.BookingInput
import com.washwise.mobile.feature.order.presenter.BookServiceContract.Service
import com.washwise.mobile.feature.order.presenter.BookServicePresenter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * View role for the Book Service screen. Owns date-picker widgets and chip state,
 * delegates all business rules to [BookServicePresenter].
 */
class BookServiceActivity : AppCompatActivity(), BookServiceContract.View {

    private lateinit var binding: ActivityBookServiceBinding
    private val presenter: BookServiceContract.Presenter = BookServicePresenter()

    private var weight = 0.0
    private val pickupCal = Calendar.getInstance()
    private val deliveryCal = Calendar.getInstance()
    private var pickupDateSet = false
    private var deliveryDateSet = false
    private var selectedPickupSlot: TimeSlot? = null
    private var selectedDeliverySlot: TimeSlot? = null
    private var minDeliveryDays = 1

    private val isoDateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val humanDateFmt = SimpleDateFormat("EEE, MMM d, yyyy", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.attach(this)
        bindListeners()
        setupWeightControls()
        setupDatePickers()
        buildTimeSlots(binding.glPickupTimes, isDelivery = false)
        buildTimeSlots(binding.glDeliveryTimes, isDelivery = true)

        presenter.load(
            presetId = intent.getStringExtra(DashboardFragment.EXTRA_SERVICE_ID),
            presetName = intent.getStringExtra(DashboardFragment.EXTRA_SERVICE_NAME)
        )
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    private fun bindListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener { presenter.submit(currentInput()) }
    }

    private fun currentInput(): BookingInput = BookingInput(
        weightKg = weight,
        pickupDateIso = if (pickupDateSet) isoDateFmt.format(pickupCal.time) else null,
        pickupSlotId = selectedPickupSlot?.id,
        address = binding.etLocation.text.toString(),
        deliveryDateIso = if (deliveryDateSet) isoDateFmt.format(deliveryCal.time) else null,
        deliverySlotId = selectedDeliverySlot?.id,
        specialInstructions = binding.etInstructions.text.toString()
    )

    private fun setupWeightControls() {
        binding.etWeight.setText("0")
        binding.btnWeightMinus.setOnClickListener { adjustWeight(delta = -0.5) }
        binding.btnWeightPlus.setOnClickListener { adjustWeight(delta = 0.5) }
        binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                val parsed = s?.toString()?.toDoubleOrNull() ?: 0.0
                weight = parsed.coerceIn(0.0, MAX_WEIGHT)
                presenter.onWeightChanged(weight)
            }
        })
    }

    private fun adjustWeight(delta: Double) {
        weight = (weight + delta).coerceIn(0.0, MAX_WEIGHT)
        binding.etWeight.setText(formatWeight(weight))
        binding.etWeight.setSelection(binding.etWeight.text.length)
        presenter.onWeightChanged(weight)
    }

    private fun formatWeight(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString()
        else String.format(Locale.US, "%.1f", value)

    private fun setupDatePickers() {
        binding.tvPickupDate.setOnClickListener { showPickupDatePicker() }
        binding.tvDeliveryDate.setOnClickListener { showDeliveryDatePicker() }
    }

    private fun showPickupDatePicker() {
        val today = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, y, m, d ->
                pickupCal.set(y, m, d, 0, 0, 0)
                pickupDateSet = true
                binding.tvPickupDate.text = humanDateFmt.format(pickupCal.time)
                ensureDeliveryAfterPickup()
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = today.timeInMillis
            show()
        }
    }

    private fun showDeliveryDatePicker() {
        if (!pickupDateSet) {
            Toast.makeText(this, "Select pickup date first", Toast.LENGTH_SHORT).show()
            return
        }
        val min = (pickupCal.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, minDeliveryDays)
        }
        val initial = if (deliveryDateSet) deliveryCal else min
        DatePickerDialog(
            this,
            { _, y, m, d ->
                deliveryCal.set(y, m, d, 0, 0, 0)
                deliveryDateSet = true
                binding.tvDeliveryDate.text = humanDateFmt.format(deliveryCal.time)
            },
            initial.get(Calendar.YEAR),
            initial.get(Calendar.MONTH),
            initial.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = min.timeInMillis
            show()
        }
    }

    private fun ensureDeliveryAfterPickup() {
        val min = (pickupCal.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, minDeliveryDays)
        }
        if (!deliveryDateSet || deliveryCal.before(min)) {
            deliveryCal.timeInMillis = min.timeInMillis
            deliveryDateSet = true
            binding.tvDeliveryDate.text = humanDateFmt.format(deliveryCal.time)
        }
    }

    private fun buildTimeSlots(grid: GridLayout, isDelivery: Boolean) {
        grid.removeAllViews()
        val cols = 3
        grid.columnCount = cols
        TIME_SLOTS.forEachIndexed { index, slot ->
            val chip = TextView(this).apply {
                text = slot.display
                setTextColor(resources.getColorStateList(R.color.time_slot_text, null))
                textSize = 14f
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.bg_time_slot)
                isClickable = true
                isFocusable = true
                setPadding(0, dp(14), 0, dp(14))
                setOnClickListener {
                    if (isDelivery) selectedDeliverySlot = slot else selectedPickupSlot = slot
                    for (i in 0 until grid.childCount) {
                        grid.getChildAt(i).isSelected = i == index
                    }
                }
            }
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(index % cols, 1f)
                rowSpec = GridLayout.spec(index / cols)
                setMargins(dp(4), dp(4), dp(4), dp(4))
            }
            grid.addView(chip, params)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    // region View contract
    override fun showServicesLoading() {
        binding.progressServices.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false
    }

    override fun hideServicesLoading() {
        binding.progressServices.visibility = View.GONE
        binding.btnSubmit.isEnabled = true
    }

    override fun renderService(service: Service) {
        binding.tvHeaderTitle.text = service.name
        binding.tvServiceNameDisplay.text = service.name
        binding.tvServiceDescDisplay.text = service.description
        binding.tvServicePriceDisplay.text = String.format(Locale.US, "₱%.0f", service.pricePerKg)
        if (service.iconRes != 0) {
            binding.ivServiceIcon.setImageResource(service.iconRes)
        }
        binding.tvMinTurnaround.text = "Min ${service.minDeliveryDays} day(s)"
        minDeliveryDays = service.minDeliveryDays
    }

    override fun showServiceUnavailable(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSubmitting() {
        binding.btnSubmit.isEnabled = false
        binding.progressSubmit.visibility = View.VISIBLE
    }

    override fun hideSubmitting() {
        binding.btnSubmit.isEnabled = true
        binding.progressSubmit.visibility = View.GONE
    }

    override fun showSuccess() {
        Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_LONG).show()
        setResult(RESULT_OK)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun updateTotal(total: Double) {
        binding.tvTotal.text = String.format(Locale.US, "₱%.0f", total)
    }

    override fun close() {
        finish()
    }
    // endregion

    data class TimeSlot(val id: String, val display: String)

    companion object {
        private const val MAX_WEIGHT = 50.0

        private val TIME_SLOTS = listOf(
            TimeSlot("8-10", "09:00"),
            TimeSlot("10-12", "11:00"),
            TimeSlot("12-14", "13:00"),
            TimeSlot("14-16", "15:00"),
            TimeSlot("16-18", "17:00"),
            TimeSlot("18-20", "19:00")
        )
    }
}
