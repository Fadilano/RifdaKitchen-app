package com.submission.rifda_kitchen.view

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.adapter.OrderAdapter
import com.submission.rifda_kitchen.databinding.ActivityOrderBinding
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.CartViewmodel
import com.submission.rifda_kitchen.viewModel.OrderViewmodel
import com.submission.rifda_kitchen.viewModel.UserViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory
import java.text.SimpleDateFormat
import java.util.Locale


class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var repository: Repository

    private val orderViewmodel: OrderViewmodel by viewModels { ViewmodelFactory(repository) }
    private val cartViewmodel: CartViewmodel by viewModels { ViewmodelFactory(repository) }

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = Repository()

        val cartItems: List<CartModel>? = intent.getParcelableArrayListExtra("cartItems")
        val totalPrice: Int = intent.getIntExtra("totalPrice", 0)
        val custName = intent.getStringExtra("name")
        val userId = intent.getStringExtra("uid")
        val custEmail = intent.getStringExtra("email")
        val custPhone = intent.getStringExtra("phone")
        val custAddress = intent.getStringExtra("address")

        binding.custName.setText(custName)
        binding.custPhone.setText(custPhone)
        binding.custAddress.setText(custAddress)

        cartItems?.let { displayCartItems(it) }

        binding.tvTotal.formatPrice(totalPrice)
        setupDatePicker()

        binding.btnOrder.setOnClickListener {

            val custName = binding.custName.text.toString().trim()
            val custAddress = binding.custAddress.text.toString().trim()
            val custPhone = binding.custPhone.text.toString().trim()
            val orderDate = binding.orderDate.text.toString().trim()


            if (custName.isNotEmpty() && custAddress.isNotEmpty() && custPhone.isNotEmpty() && orderDate.isNotEmpty()) {


                val order = OrderModel(
                    orderId = "",
                    userId = userId,
                    cartItems = cartItems,
                    totalPrice = totalPrice,
                    name = custName,
                    address = custAddress,
                    phone = custPhone,
                    date = orderDate,
                    email = custEmail,
                    paymentLink = "",
                    orderStatus = "Menunggu Konfirmasi"
                )
                orderViewmodel.saveOrder(order)
                if (userId != null) {
                    orderViewmodel.updateUserDetails(userId, custPhone, custAddress)
                }


            } else {
                Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        observeOrderResult()
    }


    private fun observeOrderResult() {
        orderViewmodel.orderResult.observe(this) { result ->
            val (success, message) = result
            if (success) {
                // Handle success
                Toast.makeText(this, "Order saved successfully", Toast.LENGTH_SHORT).show()
                cartViewmodel.removeAllItems()
                finish()
            } else {
                // Handle failure
                Toast.makeText(this, "Failed to save order: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun displayCartItems(cartItems: List<CartModel>) {
        val adapter = OrderAdapter(cartItems)
        binding.rvOrder.layoutManager = LinearLayoutManager(this)
        binding.rvOrder.adapter = adapter
    }

    private fun setupDatePicker() {
        val dateInput = binding.orderDate
        dateInput.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                binding.orderDate.setText(dateFormat.format(calendar.time)) // Update date input directly
            },
            year,
            month,
            day
        )

        // Get current date in milliseconds
        val currentDateInMillis = System.currentTimeMillis()

        // Set the minimum date to current date to prevent selecting past dates
        datePicker.datePicker.minDate = currentDateInMillis

        datePicker.show()
    }
}
