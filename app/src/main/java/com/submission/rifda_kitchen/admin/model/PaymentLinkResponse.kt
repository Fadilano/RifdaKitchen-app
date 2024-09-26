package com.submission.rifda_kitchen.admin.model

data class PaymentLinkResponse(
    val status_code: String,
    val status_message: String,
    val transaction_id: String,
    val order_id: String,
    val payment_url: String // This is the important part to show to the user
)