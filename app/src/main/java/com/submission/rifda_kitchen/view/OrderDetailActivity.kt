package com.submission.rifda_kitchen.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.adapter.OrderAdapter
import com.submission.rifda_kitchen.admin.retrofit.ApiClient
import com.submission.rifda_kitchen.databinding.ActivityOrderDetailBinding
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.model.OrderModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val order = intent.getParcelableExtra<OrderModel>("order")

        order?.let {
            displayOrderDetails(it)
            displayCartItems(order.cartItems!!)
        }
    }

    private fun displayCartItems(cartItems: List<CartModel>) {
        val adapter = OrderAdapter(cartItems)
        binding.rvOrderDetail.layoutManager = LinearLayoutManager(this)
        binding.rvOrderDetail.adapter = adapter
    }

    private fun displayOrderDetails(order: OrderModel) {
        binding.tvCustName.text = order.name
        binding.tvCustPhone.text = order.phone
        binding.tvCustAddress.text = order.address
        binding.tvOrderDate.text = order.date
        binding.tvOrderStatus.text = if (order.confirmationStatus) "Confirmed" else "Pending"
        binding.tvOrderTotal.formatPrice(order.totalPrice)
        if (order.paymentLink != null && order.paymentLink!!.isNotEmpty()) {
            binding.btnBayar.visibility = android.view.View.VISIBLE
            binding.btnBayar.setOnClickListener {
                // Open the payment link in browser
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(order.paymentLink))
                startActivity(browserIntent)
            }
        } else {
            binding.btnBayar.visibility = android.view.View.GONE
        }

    }




}
