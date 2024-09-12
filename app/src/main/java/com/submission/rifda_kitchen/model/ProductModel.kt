package com.submission.rifda_kitchen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface ProductModel {

    val name: String
    val description: String
    val price: Int
    val image_url: String
}

@Parcelize
data class MakananBeratModel(
    override val name: String = "",
    override val description: String = "",
    override val price: Int = 0,
    override val image_url: String = ""
) : ProductModel, Parcelable

@Parcelize
data class MakananRinganModel(
    override val name: String = "",
    override val description: String = "",
    override val price: Int = 0,
    override val image_url: String = ""
) : ProductModel, Parcelable