package com.example.eshop.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.lifecycle.LiveData
import androidx.room.OnConflictStrategy.Companion.REPLACE

@Dao
interface FavoriteDao {

    @Insert(onConflict = REPLACE)
    fun saveProduct(productModel: Product)

    @Query("SELECT * FROM Product")
    fun getAllFavoriteProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM Product WHERE id =:id")
    suspend fun getSpecificFavoriteProduct(id: String): Product?

    @Query("SELECT * FROM Product WHERE id =:id")
    fun getSpecificFavoriteProductLiveData(id: String): LiveData<Product?>

    @Delete
    suspend fun removeProductFromFavorites(productModel: Product)

    @Query("DELETE FROM Product")
    suspend fun deleteAllProducts()
}