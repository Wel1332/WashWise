package com.washwise.mobile.feature.order.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private var services: List<ServiceResponse> = emptyList()
    private var selectedService: ServiceResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener { submitOrder() }

        loadServices()
    }

    private fun loadServices() {
        binding.progressServices.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getActiveServices()
                if (response.isSuccessful && response.body()?.success == true) {
                    services = response.body()?.data ?: emptyList()
                    setupServiceSpinner()
                } else {
                    Toast.makeText(this@BookServiceActivity, "Failed to load services", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BookServiceActivity, "Network error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressServices.visibility = View.GONE
            }
        }
    }

    private fun setupServiceSpinner() {
        val serviceNames = services.map { "${it.name} — ₱${String.format("%.2f", it.price)}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, serviceNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerService.adapter = adapter

        binding.spinnerService.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedService = services[position]
                binding.tvServicePrice.text = "Price: ₱${String.format("%.2f", selectedService!!.price)}"
                binding.btnSubmit.isEnabled = true
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedService = null
                binding.btnSubmit.isEnabled = false
            }
        }
    }

    private fun submitOrder() {
        val service = selectedService ?: run {
            Toast.makeText(this, "Please select a service", Toast.LENGTH_SHORT).show()
            return
        }

        val location = binding.etLocation.text.toString().trim()
        if (location.isEmpty()) {
            binding.etLocation.error = "Location is required"
            return
        }

        val dateStr = binding.etScheduleDate.text.toString().trim()
        val scheduledDate = if (dateStr.isNotEmpty()) {
            "${dateStr}T09:00:00"
        } else {
            LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }

        val notes = binding.etNotes.text.toString().trim().ifEmpty { null }

        val request = CreateOrderRequest(
            serviceId = service.id,
            totalPrice = service.price,
            location = location,
            scheduledDate = scheduledDate,
            notes = notes
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
