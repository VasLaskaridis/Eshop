package com.example.eshop.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
    val userUid: String = "",
    val userName: String,
    val userImage: String,
    val userLocationName: String,
) : Parcelable