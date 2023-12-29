package com.example.eshop.models

import android.os.Parcelable
import com.example.eshop.db.Product
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val orderId: String,
    val userUid: String,
    val orderSubmittedTime: Long,
    val orderLocation: String,
    val orderStatus: OrderEnums,
    val totalCost: Float,
    val productsList: List<Product>
): Parcelable