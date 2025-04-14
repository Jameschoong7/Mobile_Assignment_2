package com.cet3014n.assignment1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderManagementAdapter(
    private var orders: List<Order> = emptyList(),
    private val onStatusChangeClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderManagementAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.order_id)
        val customerName: TextView = view.findViewById(R.id.customer_name)
        val totalAmount: TextView = view.findViewById(R.id.total_amount)
        val status: TextView = view.findViewById(R.id.order_status)
        val changeStatusButton: Button = view.findViewById(R.id.change_status_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_management, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.orderId.text = "Order #${order.orderId}"
        holder.customerName.text = "Customer ID: ${order.userId}"
        holder.totalAmount.text = "Total: RM ${String.format("%.2f", order.total)}"
        holder.status.text = "Status: ${order.status.name}"
        
        holder.changeStatusButton.setOnClickListener {
            onStatusChangeClick(order)
        }
    }

    override fun getItemCount() = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
} 