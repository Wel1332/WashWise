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
import androidx.lifecycle.lifecycleScope
import com.washwise.mobile.R
import com.washwise.mobile.databinding.ActivityBookServiceBinding
import com.washwise.mobile.feature.order.data.CreateOrderRequest
import com.washwise.mobile.feature.service.data.ServiceResponse
import com.washwise.mobile.shared.api.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookServiceBinding
    private var selectedService: ServiceResponse? = null

    private val presetName: String?
        get() = intent.getStringExtra("SERVICE_NAME")
    private val presetId: String?
        get() = intent.getStringExtra("SERVICE_ID")

    private var weight = 0.0
    private val pickupCal = Calendar.getInstance()
    private val deliveryCal = Calendar.getInstance()
    private var pickupDateSet = false
    private var deliveryDateSet = false
    private var selectedPickupSlot: TimeSlot? = null
    private var selectedDeliverySlot: TimeSlot? = null

    private val isoDateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val humanDateFmt = SimpleDateFormat("EEE, MMM d, yyyy", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener { submitOrder() }

        setupWeightControls()
        setupDatePickers()
        buildTimeSlots(binding.glPickupTimes, isDelivery = false)
        buildTimeSlots(binding.glDeliveryTimes, isDelivery = true)
        setupHeaderFromIntent()
        loadService()
    }

    private fun setupHeaderFromIntent() {
        val fallbackName = presetName ?: "Book Service"
        binding.tvHeaderTitle.text = fallbackName
        binding.tvServiceNameDisplay.text = fallbackName
        val preset = PresetService.fromName(fallbackName)
        binding.tvServiceDescDisplay.text = preset?.description ?: "Professional laundry care"
        binding.tvServicePriceDisplay.text = String.format(Locale.US, "₱%.0f", preset?.pricePerKg ?: 0.0)
        binding.ivServiceIcon.setImageResource(preset?.iconRes ?: R.drawable.ic_droplet)
        binding.tvMinTurnaround.text = "Min ${preset?.minDeliveryDays ?: 1} day(s)"
    }

    private fun setupWeightControls() {
        binding.etWeight.setText("0")
        binding.etWeight.setSelection(binding.etWeight.text.length)
        binding.btnWeightMinus.setOnClickListener {
            val next = (weight - 0.5).coerceAtLeast(0.0)
            weight = next
            binding.etWeight.setText(formatWeight(weight))
        }
        binding.btnWeightPlus.setOnClickListener {
            val next = (weight + 0.5).coerceAtMost(50.0)
            weight = next
            binding.etWeight.setText(formatWeight(weight))
        }
        binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                weight = s?.toString()?.toDoubleOrNull()?.coerceIn(0.0, 50.0) ?: 0.0
                updateTotal()
            }
        })
    }

    private fun formatWeight(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString() else String.format(Locale.US, "%.1f", value)

    private fun setupDatePickers() {
        binding.tvPickupDate.setOnClickListener {
            val today = Calendar.getInstance()
            val picker = DatePickerDialog(
                this,
                { _, y, m, d ->
                    pickupCal.set(y, m, d, 0, 0, 0)
                    pickupDateSet = true
                    binding.tvPickupDate.text = humanDateFmt.format(pickupCal.time)

                    val minDays = PresetService.fromName(presetName ?: "")?.minDeliveryDays ?: 1
                    val minDelivery = pickupCal.clone() as Calendar
                    minDelivery.add(Calendar.DAY_OF_MONTH, minDays)
                    if (!deliveryDateSet || deliveryCal.before(minDelivery)) {
                        deliveryCal.timeInMillis = minDelivery.timeInMillis
                        deliveryDateSet = true
                        binding.tvDeliveryDate.text = humanDateFmt.format(deliveryCal.time)
                    }
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            )
            picker.datePicker.minDate = today.timeInMillis
            picker.show()
        }

        binding.tvDeliveryDate.setOnClickListener {
            if (!pickupDateSet) {
                Toast.makeText(this, "Select pickup date first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val minDays = PresetService.fromName(presetName ?: "")?.minDeliveryDays ?: 1
            val min = pickupCal.clone() as Calendar
            min.add(Calendar.DAY_OF_MONTH, minDays)

            val initial = if (deliveryDateSet) deliveryCal else min
            val picker = DatePickerDialog(
                this,
                { _, y, m, d ->
                    deliveryCal.set(y, m, d, 0, 0, 0)
                    deliveryDateSet = true
                    binding.tvDeliveryDate.text = humanDateFmt.format(deliveryCal.time)
                },
                initial.get(Calendar.YEAR),
                initial.get(Calendar.MONTH),
                initial.get(Calendar.DAY_OF_MONTH)
            )
            picker.datePicker.minDate = min.timeInMillis
            picker.show()
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
            }
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(index % cols, 1f)
                rowSpec = GridLayout.spec(index / cols)
                setMargins(dp(4), dp(4), dp(4), dp(4))
            }
            chip.layoutParams = params
            chip.setOnClickListener {
                if (isDelivery) selectedDeliverySlot = slot else selectedPickupSlot = slot
                for (i in 0 until grid.childCount) {
                    grid.getChildAt(i).isSelected = i == index
                }
            }
            grid.addView(chip)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun loadService() {
        val id = presetId
        if (id.isNullOrBlank()) {
            updateTotal()
            return
        }
        binding.progressServices.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getActiveServices()
                val list = response.body()?.data.orEmpty()
                selectedService = list.firstOrNull { it.id == id }
                    ?: list.firstOrNull { it.name.equals(presetName, ignoreCase = true) }
                selectedService?.let {
                    binding.tvHeaderTitle.text = it.name
                    binding.tvServiceNameDisplay.text = it.name
                    val preset = PresetService.fromName(it.name)
                    binding.tvServiceDescDisplay.text = it.description
                        ?: preset?.description ?: "Professional laundry care"
                    binding.tvServicePriceDisplay.text = String.format(Locale.US, "₱%.0f", it.price)
                    binding.ivServiceIcon.setImageResource(preset?.iconRes ?: R.drawable.ic_droplet)
                    binding.tvMinTurnaround.text = "Min ${preset?.minDeliveryDays ?: 1} day(s)"
                }
            } catch (_: Exception) {
                // keep preset display
            } finally {
                binding.progressServices.visibility = View.GONE
                updateTotal()
            }
        }
    }

    private fun pricePerKg(): Double =
        selectedService?.price ?: PresetService.fromName(presetName ?: "")?.pricePerKg ?: 0.0

    private fun updateTotal() {
        val total = pricePerKg() * weight
        binding.tvTotal.text = String.format(Locale.US, "₱%.0f", total)
    }

    private fun submitOrder() {
        val serviceId = selectedService?.id ?: presetId
        if (serviceId.isNullOrBlank()) {
            Toast.makeText(this, "Missing service. Please go back and pick one.", Toast.LENGTH_SHORT).show()
            return
        }
        if (weight <= 0.0) {
            Toast.makeText(this, "Please enter a weight greater than 0", Toast.LENGTH_SHORT).show()
            return
        }
        if (!pickupDateSet) {
            Toast.makeText(this, "Please select a pickup date", Toast.LENGTH_SHORT).show()
            return
        }
        val pickupSlot = selectedPickupSlot
        if (pickupSlot == null) {
            Toast.makeText(this, "Please select a pickup time", Toast.LENGTH_SHORT).show()
            return
        }
        val address = binding.etLocation.text.toString().trim()
        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter your pickup address", Toast.LENGTH_SHORT).show()
            return
        }
        if (!deliveryDateSet) {
            Toast.makeText(this, "Please select a delivery date", Toast.LENGTH_SHORT).show()
            return
        }
        val deliverySlot = selectedDeliverySlot
        if (deliverySlot == null) {
            Toast.makeText(this, "Please select a delivery time", Toast.LENGTH_SHORT).show()
            return
        }

        val total = pricePerKg() * weight
        val instructions = binding.etInstructions.text.toString().trim()

        val request = CreateOrderRequest(
            serviceId = serviceId,
            totalPrice = total,
            pickupAddress = address,
            pickupDate = isoDateFmt.format(pickupCal.time),
            pickupTimeSlot = pickupSlot.id,
            deliveryDate = isoDateFmt.format(deliveryCal.time),
            deliveryTimeSlot = deliverySlot.id,
            weightKg = weight,
            specialInstructions = instructions.ifBlank { null },
            status = "PENDING"
        )

        binding.btnSubmit.isEnabled = false
        binding.progressSubmit.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.createOrder(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        this@BookServiceActivity,
                        "Booking confirmed!",
                        Toast.LENGTH_LONG
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    val msg = response.body()?.message ?: "Failed to place order"
                    Toast.makeText(this@BookServiceActivity, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@BookServiceActivity,
                    "Network error: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.btnSubmit.isEnabled = true
                binding.progressSubmit.visibility = View.GONE
            }
        }
    }

    data class TimeSlot(val id: String, val display: String)

    data class PresetService(
        val name: String,
        val description: String,
        val pricePerKg: Double,
        val minDeliveryDays: Int,
        val iconRes: Int
    ) {
        companion object {
            private val ALL = listOf(
                PresetService("Wash Only", "Basic washing for everyday items", 30.0, 1, R.drawable.ic_droplet),
                PresetService("Wash-Dry-Fold", "Complete everyday laundry care", 40.0, 2, R.drawable.ic_tshirt),
                PresetService("Dry Cleaning", "Professional care for delicates", 150.0, 3, R.drawable.ic_sparkle),
                PresetService("Premium Care", "Special handling for luxury items", 175.0, 5, R.drawable.ic_star)
            )

            fun fromName(name: String): PresetService? {
                val normalized = name.lowercase().trim()
                return ALL.firstOrNull {
                    val n = it.name.lowercase()
                    n == normalized || normalized.contains(n) || n.contains(normalized)
                }
            }
        }
    }

    companion object {
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
