package com.submission.rifda_kitchen.admin.model

data class PaymentLinkRequest(
    val payment_type: String,
    val transaction_details: TransactionDetails,
    val item_details: List<ItemDetails>,
    val customer_details: CustomerDetails,
    val custom_field1: String
)

data class TransactionDetails(
    val order_id: String,
    val gross_amount: Int
)
data class ItemDetails(
    val name: String,
    val price: Int,
    val quantity: Int,
)

data class CustomerDetails(
    val first_name: String,
    val email: String,
    val phone: String
)
