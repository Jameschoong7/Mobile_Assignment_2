package com.cet3014n.assignment1

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(private var items: List<Product>) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.item_name)
        val price: TextView = itemView.findViewById(R.id.item_price)
        val description: TextView = itemView.findViewById(R.id.item_description)
        val image: ImageView = itemView.findViewById(R.id.item_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.price.text = "RM ${String.format("%.2f", item.price)}"
        holder.description.text = item.description
        holder.image.setImageResource(item.imageResId)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CustomizeOrderActivity::class.java)
            intent.putExtra("menuItem", item)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }
}