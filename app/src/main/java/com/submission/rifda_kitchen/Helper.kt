package com.submission.rifda_kitchen

import android.icu.text.DecimalFormat
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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




