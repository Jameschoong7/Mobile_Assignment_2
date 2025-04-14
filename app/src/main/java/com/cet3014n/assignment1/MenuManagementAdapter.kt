package com.cet3014n.assignment1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuManagementAdapter(
    private var items: List<Product> = emptyList(),
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<MenuManagementAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.item_name)
        val priceTextView: TextView = view.findViewById(R.id.item_price)
        val editButton: ImageButton = view.findViewById(R.id.edit_button)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_management, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.priceTextView.text = "$${item.price}"
        
        holder.editButton.setOnClickListener { onEditClick(item) }
        holder.deleteButton.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateProducts(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }
} 