package com.cet3014n.assignment1

import androidx.room.*


// OrderItem.kt
@Entity(tableName = "order_items", foreignKeys = [
    ForeignKey(entity = Order::class, parentColumns = ["orderId"], childColumns = ["orderId"], onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = Product::class, parentColumns = ["id"], childColumns = ["productId"], onDelete = ForeignKey.CASCADE)
])
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: String,
    val productId: Long,
    val quantity: Int
)
