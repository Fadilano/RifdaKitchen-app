package com.submission.rifda_kitchen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface ProductModel {

    val name: String
    val description: String
    val price: Int
    val image_url: String
    val stock: Int
    val status: String
}

@Parcelize
data class MakananBeratModel(
    override val name: String = "",
    override val description: String = "",
    override val price: Int = 0,
    override val image_url: String = "",
    override val stock: Int = 0 // Tambahkan atribut stock
) : ProductModel, Parcelable {
    override val status: String
        get() = if (stock > 0) "Ready Stock" else "Pre Order"
}

@Parcelize
data class MakananRinganModel(
    override val name: String = "",
    override val description: String = "",
    override val price: Int = 0,
    override val image_url: String = "",
    override val stock: Int = 0 // Tambahkan atribut stock
) : ProductModel, Parcelable {
    override val status: String
        get() = if (stock > 0) "Ready Stock" else "Pre Order"
}