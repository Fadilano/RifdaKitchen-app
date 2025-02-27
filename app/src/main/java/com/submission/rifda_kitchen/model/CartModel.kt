package com.submission.rifda_kitchen.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class CartModel(
    var productId: String? = null,
    var name: String? = null,
    var price: Int = 0,
    var image_url: String? = null,
    var quantity: Int = 1,
    var category: String? = null // Tambahkan kategori produk
) : Parcelable
