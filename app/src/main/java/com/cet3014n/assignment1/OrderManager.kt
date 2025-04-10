package com.cet3014n.assignment1

object OrderManager {
    private val orders = mutableMapOf<String, Order>()

    data class Order(
        val orderId: String,
        val items: List<Pair<Product, Int>>, // Change CoffeeMenuItem to Product
        var status: String = "Preparing",
        val deliveryOption: String, // "Pickup" or "Delivery"
        val timestamp: Long = System.currentTimeMillis() // Add timestamp for sorting
    )

    fun createOrder(orderId: String, items: List<Pair<Product, Int>>, deliveryOption: String) {
        orders[orderId] = Order(orderId, items, deliveryOption = deliveryOption)
        // Simulate status updates
        simulateOrderStatusUpdates(orderId, deliveryOption)
    }

    fun getOrder(orderId: String): Order? {
        return orders[orderId]
    }

    fun getAllOrders(): List<Order> {
        return orders.values.toList()
    }

    fun completeOrder(orderId: String) {
        orders[orderId]?.status = "Completed" // Update status to "Completed"
    }

    private fun simulateOrderStatusUpdates(orderId: String, deliveryOption: String) {
        // Simulate status changes over time
        Thread {
            Thread.sleep(5000) // Wait 5 seconds
            orders[orderId]?.let {
                if (it.status != "Completed") { // Only update if not already completed
                    it.status = if (deliveryOption == "Pickup") "Ready for Pickup" else "Ready for Pickup"
                }
            }
            if (deliveryOption == "Delivery") {
                Thread.sleep(5000) // Wait another 5 seconds
                orders[orderId]?.let {
                    if (it.status != "Completed") {
                        it.status = "Out for Delivery"
                    }
                }
                Thread.sleep(5000) // Wait another 5 seconds
                orders[orderId]?.let {
                    if (it.status != "Completed") {
                        it.status = "Delivered"
                    }
                }
            }
        }.start()
    }
}
