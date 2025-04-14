package com.cet3014n.assignment1

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class FavoriteOrdersAdapter(
    private val onReorderClick: (FavoriteOrder) -> Unit,
    private val onDeleteClick: (FavoriteOrder) -> Unit
) : ListAdapter<FavoriteOrder, FavoriteOrdersAdapter.FavoriteOrderViewHolder>(FavoriteOrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_order, parent, false)
        return FavoriteOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteOrderViewHolder, position: Int) {
        val favoriteOrder = getItem(position)
        holder.bind(favoriteOrder)
    }

    fun updateFavoriteOrders(newOrders: List<FavoriteOrder>) {
        submitList(newOrders)
    }

    inner class FavoriteOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.favorite_name)
        private val dateTextView: TextView = itemView.findViewById(R.id.favorite_date)
        private val reorderButton: Button = itemView.findViewById(R.id.reorder_button)
        private val deleteButton: Button = itemView.findViewById(R.id.delete_button)

        fun bind(favoriteOrder: FavoriteOrder) {
            nameTextView.text = favoriteOrder.name
            
            // Format date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            dateTextView.text = dateFormat.format(Date(favoriteOrder.timestamp))
            
            reorderButton.setOnClickListener {
                onReorderClick(favoriteOrder)
            }
            
            deleteButton.setOnClickListener {
                onDeleteClick(favoriteOrder)
            }
        }
    }

    private class FavoriteOrderDiffCallback : DiffUtil.ItemCallback<FavoriteOrder>() {
        override fun areItemsTheSame(oldItem: FavoriteOrder, newItem: FavoriteOrder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteOrder, newItem: FavoriteOrder): Boolean {
            return oldItem == newItem
        }
    }
} 