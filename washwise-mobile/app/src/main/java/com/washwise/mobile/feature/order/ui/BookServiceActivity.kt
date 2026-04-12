package com.washwise.mobile.feature.order.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.washwise.mobile.databinding.ActivityBookServiceBinding
import com.washwise.mobile.feature.order.data.CreateOrderRequest
import com.washwise.mobile.feature.service.data.ServiceResponse
import com.washwise.mobile.shared.api.RetrofitClient
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BookServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookServiceBinding
    private var selectedService: ServiceResponse? = null
    private var currentWeight = 2.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceIdToFind = intent.getStringExtra("SERVICE_ID")

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener { submitOrder() }

        setupWeightControls()

        if (serviceIdToFind != null) {
            loadSpecializedService(serviceIdToFind)
        } else {
            Toast.makeText(this, "No service selected", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupWeightControls() {
        updateWeightDisplay()
        binding.btnWeightMinus.setOnClickListener {
            if (currentWeight > 1.0) {
                currentWeight -= 0.5
                updateWeightDisplay()
            }
        }
        binding.btnWeightPlus.setOnClickListener {
            if (currentWeight < 50.0) {
                currentWeight += 0.5
                updateWeightDisplay()
            }
        }
    }

    private fun updateWeightDisplay() {
        binding.tvWeightValue.text = String.format("%.1f", currentWeight)
        selectedService?.let {
            val total = it.price * currentWeight
            binding.tvServicePriceDisplay.text = "₱${String.format("%.2f", total)}"
        }
    }

    private fun loadSpecializedService(serviceId: String) {
        binding.progressServices.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getActiveServices()
                if (response.isSuccessful && response.body()?.success == true) {
                    val list = response.body()?.data ?: emptyList()
                    selectedService = list.find { it.id == serviceId }
                    if (selectedService != null) {
                        displayService(selectedService!!)
                    } else {
                        Toast.makeText(this@BookServiceActivity, "Service not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@BookServiceActivity, "Failed to load service data", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BookServiceActivity, "Network error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                finish()
            } finally {
                binding.progressServices.visibility = View.GONE
            }
        }
    }

    private fun displayService(service: ServiceResponse) {
        binding.tvServiceNameDisplay.text = service.name
        binding.tvServiceDescDisplay.text = service.description ?: "Professional care."
        updateWeightDisplay()
        binding.btnSubmit.isEnabled = true
    }

    private fun submitOrder() {
        val service = selectedService ?: return

        val location = binding.etLocation.text.toString().trim()
        if (location.isEmpty()) {
            binding.etLocation.error = "Location is required"
            return
        }

        val dateStr = binding.etScheduleDate.text.toString().trim()
        val scheduledDate = if (dateStr.isNotEmpty()) {
            "${dateStr}T09:00:00"
        } else {
             // Fallback default
            LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
        
        // Pass the weight info inside the notes so the UI flow doesn't lose it since the API doesn't have a weight parameter right now.
        val notesStr = "Weight: $currentWeight kg"

        val request = CreateOrderRequest(
            serviceId = service.id,
            totalPrice = service.price * currentWeight,
            location = location,
            scheduledDate = scheduledDate,
            notes = notesStr
        )

        binding.btnSubmit.isEnabled = false
        binding.progressSubmit.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.createOrder(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@BookServiceActivity, "Order placed successfully!", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    val errorMsg = response.body()?.message ?: "Failed to place order"
                    Toast.makeText(this@BookServiceActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BookServiceActivity, "Network error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnSubmit.isEnabled = true
                binding.progressSubmit.visibility = View.GONE
            }
        }
    }
}
