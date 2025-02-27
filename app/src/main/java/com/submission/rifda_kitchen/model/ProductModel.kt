package com.submission.rifda_kitchen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface ProductModel {

    val productId: String
    val name: String
    val description: String
    val price: Int
    val image_url: String
    val stock: Int
    val status: String
}

@Parcelize
data class MakananBeratModel(
    override val productId: String = "",
    override val name: String = "",
    override val description: String = "",
    override val price: Int = 0,
    override val image_url: String = "",
    override val stock: Int = 0 // Tambahkan atribut stock
) : ProductModel, Parcelable {
    override val status: String
        get() = if (stock > 0) "available" else "pre-order"
}

@Parcelize
data class MakananRinganModel(
    override val productId: String = "",
    override val name: String = "",
    override val description: String = "",
    override val price: Int = 0,
    override val image_url: String = "",
    override val stock: Int = 0 // Tambahkan atribut stock
) : ProductModel, Parcelable {
    override val status: String
        get() = if (stock > 0) "available" else "pre-order"
}