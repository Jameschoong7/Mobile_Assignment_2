package com.cet3014n.assignment1

import androidx.room.*

// User.kt
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val email: String,
    val phone: String,
    val address: String,
    var loyaltyPoints: Int = 0
)
