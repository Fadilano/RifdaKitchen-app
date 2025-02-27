package com.submission.rifda_kitchen.admin.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.rifda_kitchen.Helper.formatPrice
import com.submission.rifda_kitchen.adapter.OrderAdapter
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.model.CustomerDetails
import com.submission.rifda_kitchen.admin.model.ItemDetails
import com.submission.rifda_kitchen.admin.model.PaymentLinkRequest
import com.submission.rifda_kitchen.admin.model.TransactionDetails
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.databinding.ActivityAdminOrderDetailBinding
import com.submission.rifda_kitchen.model.CartModel
import com.submission.rifda_kitchen.model.OrderModel

class AdminOrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminOrderDetailBinding
    private lateinit var viewModel: AdminViewModel
    private var order: OrderModel? = null
    private var userId: String? = null
    private var orderId: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AdminRepository()
        viewModel =
            ViewModelProvider(this, AdminViewModelFactory(repository))[AdminViewModel::class.java]

        // Retrieve userId and orderId passed through Intent
        userId = intent.getStringExtra("userId")
        orderId = intent.getStringExtra("orderId")
        order = intent.getParcelableExtra("order")

        setSupportActionBar(binding.adminDetailToolbar)
        supportActionBar?.title = ("Order Detail")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        order?.let {
            displayOrderDetails(it)
            displayCartItems(order?.cartItems!!)
            updateButtonVisibility(order?.orderStatus!!)
        }

        // Observing ViewModel LiveData

        viewModel.errorMessage.observe(this) { message ->
            Log.e("AdminOrderDetailActivity", message)
        }

        // Event handlers

        binding.btnGeneratePaymentLink.setOnClickListener {
            order?.let {
                val paymentRequest = createPaymentLinkRequest(it)
                viewModel.createPaymentLink(paymentRequest)
            }
        }

        viewModel.paymentLinkCreated.observe(this) { link ->
            if (!link.isNullOrEmpty()) {
                // Update the payment link in Firebase
                viewModel.updatePaymentLink(userId ?: "", orderId ?: "", link)

                // Show a success message to the admin
                Toast.makeText(this, "Payment link generated and updated!", Toast.LENGTH_SHORT)
                    .show()

            }
        }

        binding.btnGeneratePaymentLink.setOnClickListener {
            order?.let {
                val paymentRequest = createPaymentLinkRequest(it)
                viewModel.createPaymentLink(paymentRequest)
                updateOrderStatus("Pesanan Dikonfirmasi, silahkan lakukan pembayaran")
            }
        }

        binding.btnProses.setOnClickListener {
            updateOrderStatus("Pesanan Diproses")
        }

        binding.btnCancel.setOnClickListener {
            showCancelConfirmationDialog()
        }

        binding.btnFinish.setOnClickListener {
            updateOrderStatus("Pesanan selesai")
        }

    }

    // Helper function to display order details
    private fun displayOrderDetails(order: OrderModel) {
        binding.tvCustName.text = order.name
        binding.tvCustPhone.text = order.phone
        binding.tvCustAddress.text = order.address
        binding.tvOrderDate.text = order.date
        binding.tvOrderTime.text = order.time
        binding.tvOrderStatus.text = order.orderStatus
        binding.tvOrderTotal.formatPrice(order.totalPrice)
    }

    private fun displayCartItems(cartItems: List<CartModel>) {
        val adapter = OrderAdapter(cartItems)
        binding.rvAdminOrderItems.layoutManager = LinearLayoutManager(this)
        binding.rvAdminOrderItems.adapter = adapter
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPaymentLinkRequest(order: OrderModel): PaymentLinkRequest {
        val items = order.cartItems?.map { cartItem ->
            ItemDetails(
                name = cartItem.name!!,
                price = cartItem.price,
                quantity = cartItem.quantity
            )
        } ?: emptyList()

        return PaymentLinkRequest(
            payment_type = "bank_transfer",
            transaction_details = TransactionDetails(
                order_id = orderId ?: "",
                gross_amount = order.totalPrice
            ),
            item_details = items,
            customer_details = CustomerDetails(
                first_name = order.name!!,
                email = order.email!!,
                phone = order.phone!!
            ),
            custom_field1 = "{\"customer_details\":{\"is_editable\":false}}"
        )
    }

    private fun deleteOrder() {
        order?.let {
            viewModel.deleteOrderWithStockUpdate(
                userId ?: "",
                orderId ?: "",
                it.cartItems!!
            )
            Toast.makeText(this, "Pesanan telah dihapus dan stok diperbarui", Toast.LENGTH_SHORT).show()
            finish() // Tutup aktivitas setelah penghapusan
        }
    }


    private fun updateOrderStatus(newStatus: String, clearPaymentLink: Boolean = false) {
        order?.let {
            viewModel.updateOrderStatus(userId ?: "", orderId ?: "", newStatus, clearPaymentLink)
            binding.tvOrderStatus.text = newStatus
            Toast.makeText(this, "Status pesanan diperbarui ke: $newStatus", Toast.LENGTH_SHORT).show()
            updateButtonVisibility(newStatus)

            // Hapus pesanan dari database jika statusnya dibatalkan
            if (newStatus == "Pesanan dibatalkan") {
                deleteOrder()
            }
        }
    }



    private fun showCancelConfirmationDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Pembatalan")
        builder.setMessage("Apakah Anda yakin ingin membatalkan pesanan ini? Stok produk akan dikembalikan.")

        builder.setPositiveButton("Ya") { _, _ ->
            deleteOrder()
        }

        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }


    private fun updateButtonVisibility(orderStatus: String) {
        when (orderStatus) {
            "Menunggu Konfirmasi" -> {
                binding.btnGeneratePaymentLink.visibility = View.VISIBLE
                binding.btnCancel.visibility = View.VISIBLE
                binding.btnProses.visibility = View.GONE
                binding.btnFinish.visibility = View.GONE
            }
            "Pesanan Dikonfirmasi, silahkan lakukan pembayaran" -> {
                binding.btnGeneratePaymentLink.visibility = View.GONE
                binding.btnCancel.visibility = View.VISIBLE
                binding.btnProses.visibility = View.VISIBLE
                binding.btnFinish.visibility = View.GONE
            }
            "Pesanan Diproses" -> {
                binding.btnGeneratePaymentLink.visibility = View.GONE
                binding.btnCancel.visibility = View.GONE
                binding.btnProses.visibility = View.GONE
                binding.btnFinish.visibility = View.VISIBLE
            }
            else -> {
                // Default: Hide all buttons if status is not matched
                binding.btnGeneratePaymentLink.visibility = View.GONE
                binding.btnCancel.visibility = View.GONE
                binding.btnProses.visibility = View.GONE
                binding.btnFinish.visibility = View.GONE
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

