package com.submission.rifda_kitchen.admin.model

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