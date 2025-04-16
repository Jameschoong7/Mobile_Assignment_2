package com.cet3014n.assignment1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    private var items: List<Pair<Product, Int>> = emptyList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.cart_item_name)
        val priceTextView: TextView = view.findViewById(R.id.cart_item_price)
        val customizationsTextView: TextView = view.findViewById(R.id.cart_item_customizations)
        val quantityTextView: TextView = view.findViewById(R.id.cart_item_quantity)
        val increaseButton: Button = view.findViewById(R.id.increase_quantity)
        val decreaseButton: Button = view.findViewById(R.id.decrease_quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (item, quantity) = items[position]
        
        holder.nameTextView.text = item.name
        holder.priceTextView.text = "RM ${String.format("%.2f", item.price * quantity)}"
        holder.customizationsTextView.text = item.description
        holder.quantityTextView.text = quantity.toString()

        holder.increaseButton.setOnClickListener {
            CartManager.updateQuantity(item, quantity + 1)
            updateItems(CartManager.getItems())
        }

        holder.decreaseButton.setOnClickListener {
            CartManager.updateQuantity(item, quantity - 1)
            updateItems(CartManager.getItems())
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Pair<Product, Int>>) {
        items = newItems
        notifyDataSetChanged()
    }
}
