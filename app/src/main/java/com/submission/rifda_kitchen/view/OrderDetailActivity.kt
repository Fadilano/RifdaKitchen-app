package com.submission.rifda_kitchen.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.adapter.OrderAdapter
import com.submission.rifda_kitchen.admin.retrofit.ApiClient
import com.submission.rifda_kitchen.databinding.ActivityOrderDetailBinding
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.model.OrderModel
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.DetailViewmodel
import com.submission.rifda_kitchen.viewModel.UserViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailActivity : AppCompatActivity() {

    private val repository = Repository()
    private lateinit var binding: ActivityOrderDetailBinding
    private val detailViewmodel: DetailViewmodel by viewModels{ ViewmodelFactory(repository) }
    private var order: OrderModel? = null
    private var userId: String? = null
    private var orderId: String? = null



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId")
        orderId = intent.getStringExtra("orderId")
        order = intent.getParcelableExtra("order")

        order?.let {
            displayOrderDetails(it)
            displayCartItems(order?.cartItems!!)
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ("Order Detail")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnCancel.setOnClickListener {
            Log.d("OrderDetailActivity", "Cancel button clicked")
            order?.let { showCancelConfirmationDialog(it) }
        }

        if (order?.orderStatus == "Pesanan dibatalkan") {
            binding.btnCancel.visibility = android.view.View.GONE
            binding.btnBayar.visibility = android.view.View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
        binding.tvOrderStatus.text = order.orderStatus
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

    private fun showCancelConfirmationDialog(order: OrderModel) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Pembatalan")
        builder.setMessage("Apakah Anda yakin ingin membatalkan pesanan ini?")

        builder.setPositiveButton("Ya") { _, _ ->
            updateOrderStatus("Pesanan dibatalkan")
        }

        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun updateOrderStatus(newStatus: String) {
        order?.let {
            detailViewmodel.updateOrderStatus(userId ?: "", orderId ?: "", newStatus)
            binding.tvOrderStatus.text = newStatus
            showToast("Status pesanan diperbarui ke: $newStatus")
        }
    }






    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }




}
