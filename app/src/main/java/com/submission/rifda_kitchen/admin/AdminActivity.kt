package com.submission.rifda_kitchen.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.adapter.HistoryAdapter
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.databinding.ActivityAdminBinding
import com.submission.rifda_kitchen.model.OrderModel

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var adminViewModel: AdminViewModel  // Initialize ViewModel later
    private val orderList = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.adminToolbar)
        supportActionBar?.title = ("Admin Panel")
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Set up ViewModel after onCreate
        val repository = AdminRepository()
        val factory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(this, factory)[AdminViewModel::class.java]

        binding.rvOrders.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        observeViewModel()
    }


    private fun observeViewModel() {
        adminViewModel.getTotalOrders().observe(this) { totalOrders ->
            binding.tvTotalOrders.text = "Total Orders: $totalOrders"
        }

        adminViewModel.getAllOrders().observe(this) { orders ->
            orderList.clear()
            orderList.addAll(orders)
            setupOrderAdapter()
        }
    }

    private fun setupOrderAdapter() {
        val adapter = HistoryAdapter(orderList) { order ->
            val intent = Intent(this, AdminOrderDetailActivity::class.java)
            intent.putExtra("order", order)
            intent.putExtra("userId", order.userId)
            intent.putExtra("orderId", order.orderId)
            startActivity(intent)
        }
        binding.rvOrders.adapter = adapter
    }
}
