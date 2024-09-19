package com.submission.rifda_kitchen.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class OrderModel(
    var cartItems: List<CartModel>? = emptyList(),
    var totalPrice: Int = 0,
    var name: String? = null,
    var address: String? = null,
    var phone: String? = null,
    var date: String? = null,
    var confirmationStatus:Boolean = false,
    var paymentLink: String? = null,
    var receipt: String? = null,
    var paymentStatus: Boolean = false,
) : Parcelable
