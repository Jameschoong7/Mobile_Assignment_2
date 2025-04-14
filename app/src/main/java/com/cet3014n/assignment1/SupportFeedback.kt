package com.cet3014n.assignment1

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "support_feedback",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SupportFeedback(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val issueType: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
) 