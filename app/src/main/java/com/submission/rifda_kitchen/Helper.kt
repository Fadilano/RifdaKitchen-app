package com.submission.rifda_kitchen

import android.icu.text.DecimalFormat
import android.widget.TextView
import com.submission.rifda_kitchen.model.CartModel

object Helper {
    fun TextView.formatPrice(value: Int?) {
        this.text = getCurrentIdr(java.lang.Double.parseDouble(value.toString()))
    }

    private fun getCurrentIdr(price: Double): CharSequence? {
        val format  = DecimalFormat("#,###,###")
        return "IDR " + format.format(price).replace(",". toRegex(), ".")
    }

}



object CartManager {
    private val cartItems = mutableListOf<CartModel>()

    fun addToCart(cartItem: CartModel) {
        val existingItem = cartItems.find { it.product.name == cartItem.product.name }
        if (existingItem != null) {
            // If the item already exists in the cart, increase the quantity
            existingItem.quantity += cartItem.quantity
        } else {
            // Add the new item to the cart
            cartItems.add(cartItem)
        }
    }

    fun getCartItems(): List<CartModel> {
        return cartItems
    }

    fun clearCart() {
        cartItems.clear()
    }
}
