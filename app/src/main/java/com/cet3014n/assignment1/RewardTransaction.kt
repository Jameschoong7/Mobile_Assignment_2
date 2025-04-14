package com.cet3014n.assignment1

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reward_transactions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RewardTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val points: Int,
    val type: TransactionType,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TransactionType {
    EARNED, REDEEMED
} 