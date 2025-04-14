package com.cet3014n.assignment1

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class OrderManagementFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderManagementAdapter
    private lateinit var repository: CoffeeShopRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_management, container, false)
        
        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(requireContext()).coffeeShopDao())
        
        recyclerView = view.findViewById(R.id.orders_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = OrderManagementAdapter(
            orders = emptyList(),
            onStatusChangeClick = { order -> showStatusChangeDialog(order) }
        )
        recyclerView.adapter = adapter
        
        loadOrders()
        
        return view
    }

    private fun loadOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val orders = repository.getAllOrders()
                adapter.updateOrders(orders)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading orders: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showStatusChangeDialog(order: Order) {
        val statuses = OrderStatus.values()
        val statusNames = statuses.map { it.name }.toTypedArray()
        
        AlertDialog.Builder(requireContext())
            .setTitle("Change Order Status")
            .setItems(statusNames) { _, which ->
                val newStatus = statuses[which]
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val updatedOrder = order.copy(status = newStatus)
                        repository.updateOrder(updatedOrder)
                        loadOrders()
                        Toast.makeText(requireContext(), "Order status updated", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error updating order status: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
} 