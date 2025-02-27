package com.submission.rifda_kitchen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var uid: String? = "",
    var name: String? = "",
    var email: String? = "",
    var photo_url: String? = "",
    var phone: String? = null,
    var address: String? = null,
    val role: String = "User"
) : Parcelable
