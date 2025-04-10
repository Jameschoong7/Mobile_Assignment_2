package com.cet3014n.assignment1

import java.io.Serializable

data class CoffeeMenuItem(
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val dietary: List<String>,
    val imageResId: Int
) : Serializable