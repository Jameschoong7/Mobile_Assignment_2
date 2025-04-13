package com.cet3014n.assignment1

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_order_items",
    foreignKeys = [
        ForeignKey(
            entity = FavoriteOrder::class,
            parentColumns = ["id"],
            childColumns = ["favoriteOrderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FavoriteOrderItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val favoriteOrderId: Long,
    val productId: Long,
    val quantity: Int
)