package com.submission.rifda_kitchen.admin.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.adapter.HistoryAdapter
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.databinding.ActivityAdmiinOrderListBinding
import com.submission.rifda_kitchen.model.OrderModel

class AdminOrderListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdmiinOrderListBinding
    private val repository = AdminRepository()
    private val adminViewModel: AdminViewModel by viewModels { AdminViewModelFactory(repository) }

    // Initialize ViewModel later
    private val orderList = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdmiinOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.OrderListToolbar)
        supportActionBar?.title = ("Order List")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        binding.rvOrders.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        observeViewModel()
    }

    private fun observeViewModel() {
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
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
