package com.cet3014n.assignment1


import androidx.room.*

// Order.kt
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val orderId: String,
    val userId: Long,
    val status: OrderStatus = OrderStatus.PREPARING,
    val deliveryOption: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class OrderStatus {
    PREPARING, COMPLETED, CANCELLED
}
