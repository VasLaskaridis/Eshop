package com.example.eshop.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Product(
    @PrimaryKey
    val id: String,
    val name: String,
    val image: String,
    val calories: Double,
    val carb: Double,
    val fat: Double,
    val detail: String,
    val protein: Double,
    val price: Double,
    var quantity: Int,
    val quantityType: String,
    val category: String,
): Parcelable
