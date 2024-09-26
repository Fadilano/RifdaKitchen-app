package com.submission.rifda_kitchen.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.submission.rifda_kitchen.adapter.HistoryAdapter
import com.submission.rifda_kitchen.databinding.ActivityAdminBinding
import com.submission.rifda_kitchen.model.OrderModel

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var database: DatabaseReference
    private val orderList = mutableListOf<OrderModel>()
    private var totalUsers = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        // Set up RecyclerView
        binding.rvOrders.layoutManager = LinearLayoutManager(this)

        // Fetch total users

        // Fetch total orders
        database.child("orders").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvTotalOrders.text = "Total Orders: ${snapshot.childrenCount}"
                for (orderSnapshot in snapshot.children) {
                    for (orderDetailSnapshot in orderSnapshot.children) {
                        val order = orderDetailSnapshot.getValue(OrderModel::class.java)
                        order?.let { orderList.add(it) }
                    }
                }
                setupOrderAdapter()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun setupOrderAdapter() {
        val adapter = HistoryAdapter(orderList) { order ->
            // Navigate to OrderDetailActivity for admin to update
            val intent = Intent(this, AdminOrderDetailActivity::class.java)
            intent.putExtra("order", order)
            intent.putExtra("userId", order.userId)
            intent.putExtra("orderId", order.orderId)
            startActivity(intent)

            startActivity(intent)
        }
        binding.rvOrders.adapter = adapter
    }
}
