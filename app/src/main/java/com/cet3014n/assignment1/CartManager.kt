package com.cet3014n.assignment1

import android.util.Log

object CartManager {
    private val cartItems = mutableListOf<Pair<Product, Int>>() // Item and quantity

    fun addItem(item: Product, quantity: Int = 1) {
        // Find if the product already exists in the cart based on name and description (customized description)
        val existing = cartItems.find {
            it.first.name == item.name && it.first.description == item.description
        }
        if (existing != null) {
            cartItems[cartItems.indexOf(existing)] = existing.copy(second = existing.second + quantity)
        } else {
            cartItems.add(Pair(item, quantity))
        }
    }

    fun getItems(): List<Pair<Product, Int>> = cartItems.toList()

    fun updateQuantity(item: Product, newQuantity: Int) {
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
        Log.d("CartManager", "Clearing cart")
        cartItems.clear()
    }
}
