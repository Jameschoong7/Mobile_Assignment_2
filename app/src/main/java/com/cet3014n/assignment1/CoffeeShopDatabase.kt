package com.cet3014n.assignment1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        User::class,
        Product::class,
        Order::class,
        OrderItem::class,
        RewardTransaction::class,
        FavoriteOrder::class,
        FavoriteOrderItem::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)  // Register the TypeConverters class
abstract class CoffeeShopDatabase : RoomDatabase() {

    abstract fun coffeeShopDao(): CoffeeShopDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: CoffeeShopDatabase? = null

        fun getDatabase(context: Context): CoffeeShopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoffeeShopDatabase::class.java,
                    "coffee_shop_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
