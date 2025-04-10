package com.cet3014n.assignment1

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val dietary: List<String>,
    val imageResId: Int
): Serializable

