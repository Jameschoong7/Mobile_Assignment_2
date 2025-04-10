package com.cet3014n.assignment1

object CartManager {
    private val cartItems = mutableListOf<Pair<CoffeeMenuItem, Int>>() // Item and quantity

    fun addItem(item: CoffeeMenuItem, quantity: Int = 1) {
        val existing = cartItems.find { it.first == item }
        if (existing != null) {
            cartItems[cartItems.indexOf(existing)] = existing.copy(second = existing.second + quantity)
        } else {
            cartItems.add(Pair(item, quantity))
        }
    }

    fun getItems(): List<Pair<CoffeeMenuItem, Int>> = cartItems.toList()

    fun updateQuantity(item: CoffeeMenuItem, newQuantity: Int) {
        val index = cartItems.indexOfFirst { it.first == item }
        if (index != -1) {
            if (newQuantity > 0) {
                cartItems[index] = cartItems[index].copy(second = newQuantity)
            } else {
                cartItems.removeAt(index) // Remove item if quantity is 0
            }
        }
    }

    fun clearCart() {
        cartItems.clear()
    }
}