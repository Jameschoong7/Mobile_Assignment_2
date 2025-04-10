package com.cet3014n.assignment1

import android.content.Context
import androidx.room.Database
import androidx.room.*


@Database(entities = [User::class, Product::class, Order::class, OrderItem::class], version = 1)
abstract class CoffeeShopDatabase : RoomDatabase() {
    abstract fun coffeeShopDao(): CoffeeShopDao
}
