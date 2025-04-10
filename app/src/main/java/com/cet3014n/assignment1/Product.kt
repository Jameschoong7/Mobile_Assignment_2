package com.cet3014n.assignment1

import androidx.room.*
// Product.kt
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val dietary: List<String>,
    val image: String
)
