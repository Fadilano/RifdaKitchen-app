package com.submission.rifda_kitchen.model

data class CartModel(
    val product: ProductModel, // The product added to the cart
    var quantity: Int = 0      // The quantity of the product (default is 1)
)
