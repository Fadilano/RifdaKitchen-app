package com.submission.rifda_kitchen.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class OrderModel(
    var orderId: String? = null,
    var userId:String? = null,
    var cartItems: List<CartModel>? = emptyList(),
    var totalPrice: Int = 0,
    var name: String? = null,
    var address: String? = null,
    var phone: String? = null,
    var date: String? = null,
    var email: String? = null,
    var paymentLink: String? = null,
    var orderStatus: String = "Menunggu Konfirmasi"
    ) : Parcelable
