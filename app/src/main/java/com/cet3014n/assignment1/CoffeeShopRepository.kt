package com.cet3014n.assignment1

class CoffeeShopRepository (private val coffeeShopDao: CoffeeShopDao){
    // Insert user
    suspend fun insertUser(user: User) {
        coffeeShopDao.insertUser(user)
    }

    // Get user by id
    suspend fun getUser(userId: Long): User? {
        return coffeeShopDao.getUser(userId)
    }

    // Get user by email
    suspend fun getUserByEmail(userEmail: String): User? {
        return coffeeShopDao.getUserByEmail(userEmail)
    }

    // Insert product
    suspend fun insertProduct(product: Product) {
        coffeeShopDao.insertProduct(product)
    }

    // Get all products
    suspend fun getAllProducts(): List<Product> {
        return coffeeShopDao.getAllProducts()
    }

    // Insert order
    suspend fun insertOrder(order: Order) {
        coffeeShopDao.insertOrder(order)
    }

    // Get orders by user
    suspend fun getOrdersByUser(userId: Long): List<Order> {
        return coffeeShopDao.getOrdersByUser(userId)
    }

    // Insert order items
    suspend fun insertOrderItem(orderItem: OrderItem) {
        coffeeShopDao.insertOrderItem(orderItem)
    }

    // Get items for an order
    suspend fun getItemsForOrder(orderId: String): List<OrderItem> {
        return coffeeShopDao.getItemsForOrder(orderId)
    }
}