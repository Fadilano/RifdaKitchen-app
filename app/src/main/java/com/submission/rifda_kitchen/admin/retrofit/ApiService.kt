package com.submission.rifda_kitchen.admin.retrofit

import com.submission.rifda_kitchen.admin.model.PaymentLinkRequest
import com.submission.rifda_kitchen.admin.model.PaymentLinkResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("v1/payment-links")
    fun createPaymentLink(
        @Body paymentLinkRequest: PaymentLinkRequest
    ): Call<PaymentLinkResponse>
}
