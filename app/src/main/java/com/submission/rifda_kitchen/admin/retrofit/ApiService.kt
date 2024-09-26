package com.submission.rifda_kitchen.admin.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Data classes to represent the API request and response
data class PaymentLinkRequest(
    val payment_type: String,
    val transaction_details: TransactionDetails,
    val customer_details: CustomerDetails
)

data class TransactionDetails(
    val order_id: String,
    val gross_amount: Int
)

data class CustomerDetails(
    val first_name: String,
    val email: String,
    val phone: String
)

data class PaymentLinkResponse(
    val status_code: String,
    val status_message: String,
    val transaction_id: String,
    val order_id: String,
    val payment_url: String // This is the important part to show to the user
)

interface MidtransApiService {

    @Headers("Content-Type: application/json")
    @POST("v1/payment-links")
    fun createPaymentLink(
        @Body paymentLinkRequest: PaymentLinkRequest
    ): Call<PaymentLinkResponse>
}
