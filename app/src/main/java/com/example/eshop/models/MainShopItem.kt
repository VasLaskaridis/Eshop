package com.example.eshop.models

import android.os.Parcelable
import com.example.eshop.db.Product
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainShopItem(
    val id: String,
    val name: String,
    val sort: Int,
    var showInSimpleStyle: Boolean,
    val list: MutableList<Product> = mutableListOf()
): Parcelable
