package com.submission.rifda_kitchen.admin

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.submission.rifda_kitchen.admin.retrofit.CustomerDetails
import com.submission.rifda_kitchen.admin.retrofit.PaymentLinkRequest
import com.submission.rifda_kitchen.admin.retrofit.PaymentLinkResponse
import com.submission.rifda_kitchen.admin.retrofit.RetrofitClient
import com.submission.rifda_kitchen.admin.retrofit.TransactionDetails
import com.submission.rifda_kitchen.databinding.ActivityAdminOrderDetailBinding
import com.submission.rifda_kitchen.model.OrderModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminOrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminOrderDetailBinding
    private lateinit var database: DatabaseReference
    private var order: OrderModel? = null
    private var userId: String? = null
    private var orderId: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        // Retrieve userId and orderId passed through Intent
        userId = intent.getStringExtra("userId")
        orderId = intent.getStringExtra("orderId")
        order = intent.getParcelableExtra("order")

        order?.let {
            displayOrderDetails(it)
        }

        // Update confirmation status when button is clicked
        binding.btnUpdateStatus.setOnClickListener {
            val newStatus = !(order?.confirmationStatus ?: false) // Toggle confirmation status
            updateConfirmationStatus(newStatus)
        }

        // Generate and set payment link when button is clicked
        binding.btnGeneratePaymentLink.setOnClickListener {
            order?.let {
                createPaymentLink(it)
            }
        }
    }

    // Function to display order details in the UI
    private fun displayOrderDetails(order: OrderModel) {
        binding.tvAdminCustomerName.text = order.name
        binding.tvAdminCustomerPhone.text = order.phone
        binding.tvAdminCustomerAddress.text = order.address
        binding.tvAdminOrderStatus.text = if (order.confirmationStatus) "Confirmed" else "Pending"
        binding.etPaymentLink.setText(order.paymentLink ?: "")
    }

    // Function to update confirmation status in Firebase
    private fun updateConfirmationStatus(confirmed: Boolean) {
        if (userId != null && orderId != null) {
            val orderRef = database.child("orders").child(userId!!).child(orderId!!)
            orderRef.child("confirmationStatus").setValue(confirmed)
                .addOnSuccessListener {
                    binding.tvAdminOrderStatus.text = if (confirmed) "Confirmed" else "Pending"
                }
                .addOnFailureListener {
                    Log.e("Admin", "Failed to update confirmation status: ${it.message}")
                }
        }
    }

    // Function to create a payment link via the Midtrans API
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPaymentLink(order: OrderModel) {
        val midtransApiService = RetrofitClient.getMidtransService()

        val paymentRequest = PaymentLinkRequest(
            payment_type = "bank_transfer", // or other payment types
            transaction_details = TransactionDetails(
                order_id = orderId ?: "", // orderId from intent
                gross_amount = order.totalPrice
            ),
            customer_details = CustomerDetails(
                first_name = order.name!!,
                email = "test@example.com",  // Assuming email is not provided, modify accordingly
                phone = "085883327454"
            )
        )

        // Make the API call
        midtransApiService.createPaymentLink(paymentRequest)
            .enqueue(object : Callback<PaymentLinkResponse> {
                override fun onResponse(
                    call: Call<PaymentLinkResponse>,
                    response: Response<PaymentLinkResponse>
                ) {
                    if (response.isSuccessful) {
                        val paymentLink = response.body()?.payment_url
                        paymentLink?.let {
                            Log.d("MidtransAPI", "Payment link generated: $it")
                            // Save the payment link to Firebase under the order
                            updatePaymentLinkInFirebase(it)
                        }
                    } else {
                        // Handle errors
                        Log.e("MidtransAPI", "Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<PaymentLinkResponse>, t: Throwable) {
                    // Handle failure
                    Log.e("MidtransAPI", "Failed to create payment link", t)
                }
            })
    }

    // Function to update payment link in Firebase
    private fun updatePaymentLinkInFirebase(paymentLink: String) {
        if (userId != null && orderId != null) {
            Log.d("Admin", "Updating payment link in Firebase for UserId: $userId, OrderId: $orderId")

            val orderRef = FirebaseDatabase.getInstance().getReference("orders")
                .child(userId!!)
                .child(orderId!!)

            orderRef.child("paymentLink").setValue(paymentLink)
                .addOnSuccessListener {
                    binding.etPaymentLink.setText(paymentLink)
                    Log.d("Admin", "Payment link updated successfully in Firebase")
                }
                .addOnFailureListener { e ->
                    Log.e("Admin", "Failed to update payment link in Firebase: ${e.message}")
                }
        } else {
            Log.e("Admin", "UserId or OrderId is null. Cannot update payment link in Firebase.")
        }
    }
}
