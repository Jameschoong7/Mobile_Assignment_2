package com.cet3014n.assignment1

import androidx.room.*

@Dao
interface ProductDao {
    @Insert
    suspend fun insertAll(vararg products: Product)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM products WHERE category = :category")
    suspend fun getProductsByCategory(category: String): List<Product>
}
