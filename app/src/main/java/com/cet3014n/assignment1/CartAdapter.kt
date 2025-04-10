package com.cet3014n.assignment1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private var items: List<Pair<Product, Int>> = CartManager.getItems()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.cart_item_name)
        val price: TextView = itemView.findViewById(R.id.cart_item_price)
        val customizations: TextView = itemView.findViewById(R.id.cart_item_customizations)
        val quantity: TextView = itemView.findViewById(R.id.cart_item_quantity)
        val decreaseButton: Button = itemView.findViewById(R.id.decrease_quantity)
        val increaseButton: Button = itemView.findViewById(R.id.increase_quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (item, quantity) = items[position]
        holder.name.text = item.name
        holder.price.text = "RM${String.format("%.2f", item.price)}"
        holder.customizations.text = item.description
        holder.quantity.text = quantity.toString()

        holder.decreaseButton.setOnClickListener {
            val newQuantity = quantity - 1
            CartManager.updateQuantity(item, newQuantity)
            items = CartManager.getItems()
            notifyDataSetChanged()
            (holder.itemView.context as? CartActivity)?.updateTotalAmount(items)
        }

        holder.increaseButton.setOnClickListener {
            val newQuantity = quantity + 1
            CartManager.updateQuantity(item, newQuantity)
            items = CartManager.getItems()
            notifyDataSetChanged()
            (holder.itemView.context as? CartActivity)?.updateTotalAmount(items)
        }
    }

    override fun getItemCount() = items.size

    fun refreshItems() {
        items = CartManager.getItems()
        notifyDataSetChanged()
    }
}
