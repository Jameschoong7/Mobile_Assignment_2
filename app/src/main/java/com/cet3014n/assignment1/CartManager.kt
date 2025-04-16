package com.cet3014n.assignment1

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object CartManager {
    private val cartItems = mutableListOf<Pair<Product, Int>>() // Item and quantity
    private var appliedDiscount: Double = 0.0
    private var appliedPromoCode: String? = null

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
        appliedDiscount = 0.0
        appliedPromoCode = null
    }

    fun getAppliedDiscount(): Double = appliedDiscount
    fun getAppliedPromoCode(): String? = appliedPromoCode

    fun applyDiscount(discount: Double, promoCode: String) {
        appliedDiscount = discount
        appliedPromoCode = promoCode
    }

    fun removeDiscount() {
        appliedDiscount = 0.0
        appliedPromoCode = null
    }

    fun calculateSubtotal(): Double {
        return cartItems.sumOf { (item, quantity) -> item.price * quantity }
    }

    fun calculateTotal(): Double {
        val subtotal = calculateSubtotal()
        return subtotal * (1 - appliedDiscount)
    }
}
