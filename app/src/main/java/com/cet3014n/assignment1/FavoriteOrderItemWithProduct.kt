package com.cet3014n.assignment1

data class FavoriteOrderItemWithProduct(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val imageResId: Int,
    val category: String,
    val dietary: List<String>,
    val quantity: Int
) {
    fun toProduct() = Product(
        id = id,
        name = name,
        description = description,
        price = price,
        imageResId = imageResId,
        category = category,
        dietary = dietary
    )
}