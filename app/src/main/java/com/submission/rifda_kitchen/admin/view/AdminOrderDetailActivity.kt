package com.submission.rifda_kitchen.admin.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModel
import com.submission.rifda_kitchen.admin.ViewModel.AdminViewModelFactory
import com.submission.rifda_kitchen.admin.model.CustomerDetails
import com.submission.rifda_kitchen.admin.model.ItemDetails
import com.submission.rifda_kitchen.admin.model.PaymentLinkRequest
import com.submission.rifda_kitchen.admin.model.TransactionDetails
import com.submission.rifda_kitchen.admin.repository.AdminRepository
import com.submission.rifda_kitchen.databinding.ActivityAdminOrderDetailBinding
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
        supportActionBar?.title = ("Admin Panel")
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        order?.let {
            displayOrderDetails(it)
        }

        // Observing ViewModel LiveData
        viewModel.confirmationStatusUpdated.observe(this) { success ->
            if (success) {
                val newStatus = !(order?.confirmationStatus ?: false)
                binding.tvAdminOrderStatus.text = if (newStatus) "Confirmed" else "Pending"
            }
        }

        viewModel.paymentLinkCreated.observe(this) { link ->
            binding.etPaymentLink.setText(link)
        }

        viewModel.errorMessage.observe(this) { message ->
            Log.e("AdminOrderDetailActivity", message)
        }

        // Event handlers
        binding.btnUpdateStatus.setOnClickListener {
            val newStatus = !(order?.confirmationStatus ?: false)
            viewModel.updateConfirmationStatus(userId ?: "", orderId ?: "", newStatus)
        }

        binding.btnGeneratePaymentLink.setOnClickListener {
            order?.let {
                val paymentRequest = createPaymentLinkRequest(it)
                viewModel.createPaymentLink(paymentRequest)
            }
        }

        viewModel.paymentLinkCreated.observe(this) { link ->
            if (!link.isNullOrEmpty()) {
                // Display the payment link
                binding.etPaymentLink.setText(link)

                // Update the payment link in Firebase
                viewModel.updatePaymentLink(userId ?: "", orderId ?: "", link)

                // Show a success message to the admin
                Toast.makeText(this, "Payment link generated and updated!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Helper function to display order details
    private fun displayOrderDetails(order: OrderModel) {
        binding.tvAdminCustomerName.text = order.name
        binding.tvAdminCustomerPhone.text = order.phone
        binding.tvAdminCustomerAddress.text = order.address
        binding.tvAdminOrderStatus.text = if (order.confirmationStatus) "Confirmed" else "Pending"
        binding.etPaymentLink.setText(order.paymentLink ?: "")
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
            )
        )
    }
}

