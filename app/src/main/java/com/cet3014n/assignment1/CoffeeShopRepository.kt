package com.cet3014n.assignment1

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class CoffeeShopRepository(private val dao: CoffeeShopDao) {
    // User operations
    suspend fun insertUser(user: User) = dao.insertUser(user)
    suspend fun updateUser(user: User) = dao.updateUser(user)
    suspend fun deleteUser(user: User) = dao.deleteUser(user)
    suspend fun getUser(userId: Long): User? = dao.getUser(userId)
    suspend fun getUserByEmail(email: String): User? = dao.getUserByEmail(email)

    // Product operations
    suspend fun insertProduct(product: Product) = dao.insertProduct(product)
    suspend fun updateProduct(product: Product) = dao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = dao.deleteProduct(product)
    suspend fun getAllProducts(): List<Product> = dao.getAllProducts()

    // Order operations
    suspend fun insertOrder(order: Order) = dao.insertOrder(order)
    suspend fun updateOrder(order: Order) = dao.updateOrder(order)
    suspend fun deleteOrder(order: Order) = dao.deleteOrder(order)
    suspend fun getOrdersByUser(userId: Long): List<Order> = dao.getOrdersByUser(userId)

    // OrderItem operations
    suspend fun insertOrderItem(orderItem: OrderItem) = dao.insertOrderItem(orderItem)
    suspend fun getItemsForOrder(orderId: String): List<OrderItem> = dao.getItemsForOrder(orderId)

    // Reward Transaction methods
    suspend fun insertRewardTransaction(transaction: RewardTransaction) {
        dao.insertRewardTransaction(transaction)
    }

    fun getRewardTransactions(userId: Long): Flow<List<RewardTransaction>> {
        return dao.getRewardTransactions(userId)
    }

    fun getTotalRewardPoints(userId: Long): Flow<Int> {
        return dao.getTotalRewardPoints(userId)
    }

    fun getRewardTransactionsByType(userId: Long, type: TransactionType): Flow<List<RewardTransaction>> {
        return dao.getRewardTransactionsByType(userId, type)
    }

    // Favorite Order operations
    suspend fun insertFavoriteOrder(favoriteOrder: FavoriteOrder, items: List<Pair<Product, Int>>): Long {
        val favoriteOrderId = dao.insertFavoriteOrder(favoriteOrder)
        for ((product, quantity) in items) {
            val favoriteOrderItem = FavoriteOrderItem(
                favoriteOrderId = favoriteOrderId,
                productId = product.id,
                quantity = quantity
            )
            dao.insertFavoriteOrderItem(favoriteOrderItem)
        }
        return favoriteOrderId
    }

    suspend fun deleteFavoriteOrder(favoriteOrder: FavoriteOrder) {
        dao.deleteFavoriteOrder(favoriteOrder)
    }

    fun getFavoriteOrders(userId: Long): Flow<List<FavoriteOrder>> = dao.getFavoriteOrders(userId)

    suspend fun getFavoriteOrderWithItems(id: Long): Pair<FavoriteOrder?, List<Pair<Product, Int>>> {
        val favoriteOrder = dao.getFavoriteOrder(id)
        val items = if (favoriteOrder != null) {
            dao.getFavoriteOrderItemsWithProducts(id).map { it.toProduct() to it.quantity }
        } else {
            emptyList()
        }
        return Pair(favoriteOrder, items)
    }

    // Support Feedback operations
    suspend fun insertSupportFeedback(feedback: SupportFeedback) = dao.insertSupportFeedback(feedback)
    suspend fun getSupportFeedback(userId: Long): List<SupportFeedback> = dao.getSupportFeedback(userId)
}