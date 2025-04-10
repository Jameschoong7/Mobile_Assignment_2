package com.cet3014n.assignment1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private var orders: List<OrderManager.Order> = emptyList()

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdText: TextView = itemView.findViewById(R.id.order_id)
        val orderStatusText: TextView = itemView.findViewById(R.id.order_status)
        val orderItemsText: TextView = itemView.findViewById(R.id.order_items)
        val trackMapButton: Button = itemView.findViewById(R.id.track_map_button)
        val markReceivedButton: Button = itemView.findViewById(R.id.mark_received_button)
        val completedLabel: TextView = itemView.findViewById(R.id.completed_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.orderIdText.text = "Order ID: ${order.orderId}"
        holder.orderStatusText.text = "Status: ${order.status}"

        // Display order items
        val itemsSummary = order.items.joinToString(", ") { (item, quantity) ->
            "${item.name} (x$quantity)"
        }
        holder.orderItemsText.text = "Items: $itemsSummary"

        // Show "Track on Map" button only for Delivery orders and if not completed
        if (order.deliveryOption == "Delivery" && order.status != "Completed") {
            holder.trackMapButton.visibility = View.VISIBLE
            holder.trackMapButton.setOnClickListener {
                // Simulate tracking on a map (e.g., open Google Maps)
                holder.itemView.context.startActivity(
                    android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://maps.google.com")
                    )
                )
            }
        } else {
            holder.trackMapButton.visibility = View.GONE
        }

        // Handle "Mark as Received" button
        if (order.status == "Completed") {
            holder.markReceivedButton.visibility = View.GONE
            holder.completedLabel.visibility = View.VISIBLE
        } else {
            holder.markReceivedButton.visibility = View.VISIBLE
            holder.completedLabel.visibility = View.GONE
            holder.markReceivedButton.setOnClickListener {
                OrderManager.completeOrder(order.orderId)
                refreshOrders() // Refresh the entire list to re-sort after status change
            }
        }
    }

    override fun getItemCount(): Int = orders.size

    fun refreshOrders() {
        // Get all orders
        val allOrders = OrderManager.getAllOrders()

        // Separate active and completed orders
        val activeOrders = allOrders.filter { it.status != "Completed" }
            .sortedByDescending { it.timestamp } // Sort active orders by timestamp (newest first)
        val completedOrders = allOrders.filter { it.status == "Completed" }
            .sortedByDescending { it.timestamp } // Sort completed orders by timestamp (newest first)

        // Combine the lists: active orders on top, completed orders at the bottom
        orders = activeOrders + completedOrders

        notifyDataSetChanged()
    }
}