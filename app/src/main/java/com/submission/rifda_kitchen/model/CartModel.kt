package com.submission.rifda_kitchen.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CartModel(
    var name: String? = null,
    var price: Int = 0,
    var quantity: Int = 1
)