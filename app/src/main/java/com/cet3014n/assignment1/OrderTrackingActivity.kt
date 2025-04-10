package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderTrackingActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var backButton: Button
    private lateinit var emptyMessage: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            orderAdapter.refreshOrders()
            // Show/hide empty message based on the number of orders
            emptyMessage.visibility = if (orderAdapter.itemCount == 0) View.VISIBLE else View.GONE
            recyclerView.visibility = if (orderAdapter.itemCount == 0) View.GONE else View.VISIBLE
            // Stop refreshing if all orders are completed
            val allCompleted = OrderManager.getAllOrders().all { it.status == "Completed" }
            if (!allCompleted) {
                handler.postDelayed(this, 2000) // Refresh every 2 seconds
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_tracking)

        supportActionBar?.apply {
            title = "Track Orders"
        }

        recyclerView = findViewById(R.id.order_recycler_view)
        emptyMessage = findViewById(R.id.empty_message)
        backButton = findViewById(R.id.back_button)
        recyclerView.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter()
        recyclerView.adapter = orderAdapter

        // Handle back button click
        backButton.setOnClickListener {
            finish()
        }

        // Start refreshing the order list
        handler.post(refreshRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Clean up the handler
    }
}