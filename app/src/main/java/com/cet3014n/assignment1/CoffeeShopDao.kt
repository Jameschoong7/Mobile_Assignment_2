package com.cet3014n.assignment1

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeShopDao {
    // User operations
    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUser(userId: Long): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    // Product operations
    @Insert
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAllProducts(): List<Product>

    // Order operations
    @Insert
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getOrdersByUser(userId: Long): List<Order>

    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    suspend fun getAllOrders(): List<Order>

    // OrderItem operations
    @Insert
    suspend fun insertOrderItem(orderItem: OrderItem)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: String): List<OrderItem>

    // Reward Transaction methods
    @Insert
    suspend fun insertRewardTransaction(transaction: RewardTransaction)

    @Query("SELECT * FROM reward_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getRewardTransactions(userId: Long): Flow<List<RewardTransaction>>

    @Query("SELECT SUM(points) FROM reward_transactions WHERE userId = :userId")
    fun getTotalRewardPoints(userId: Long): Flow<Int>

    @Query("SELECT * FROM reward_transactions WHERE userId = :userId AND type = :type ORDER BY timestamp DESC")
    fun getRewardTransactionsByType(userId: Long, type: TransactionType): Flow<List<RewardTransaction>>

    // Favorite Order methods
    @Query("SELECT * FROM favorite_orders WHERE userId = :userId ORDER BY timestamp DESC")
    fun getFavoriteOrders(userId: Long): Flow<List<FavoriteOrder>>

    @Insert
    suspend fun insertFavoriteOrder(favoriteOrder: FavoriteOrder): Long

    @Delete
    suspend fun deleteFavoriteOrder(favoriteOrder: FavoriteOrder)

    @Query("SELECT * FROM favorite_orders WHERE id = :id")
    suspend fun getFavoriteOrder(id: Long): FavoriteOrder?

    // Favorite Order Item methods
    @Insert
    suspend fun insertFavoriteOrderItem(item: FavoriteOrderItem)

    @Query("SELECT * FROM favorite_order_items WHERE favoriteOrderId = :favoriteOrderId")
    suspend fun getFavoriteOrderItems(favoriteOrderId: Long): List<FavoriteOrderItem>

    @Query("""
        SELECT p.*, foi.quantity 
        FROM products p
        INNER JOIN favorite_order_items foi ON p.id = foi.productId
        WHERE foi.favoriteOrderId = :favoriteOrderId
    """)
    suspend fun getFavoriteOrderItemsWithProducts(favoriteOrderId: Long): List<FavoriteOrderItemWithProduct>

    @Delete
    suspend fun deleteFavoriteOrderItem(item: FavoriteOrderItem)

    // Support Feedback methods
    @Insert
    suspend fun insertSupportFeedback(feedback: SupportFeedback)

    @Query("SELECT * FROM support_feedback WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getSupportFeedback(userId: Long): List<SupportFeedback>
}
