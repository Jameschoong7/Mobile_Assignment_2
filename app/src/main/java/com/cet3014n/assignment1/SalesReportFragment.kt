package com.cet3014n.assignment1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SalesReportFragment : Fragment() {
    private lateinit var repository: CoffeeShopRepository
    private lateinit var totalRevenueText: TextView
    private lateinit var totalOrdersText: TextView
    private lateinit var avgOrderValueText: TextView
    private lateinit var salesChart: LineChart
    private lateinit var topProductsRecycler: RecyclerView
    private lateinit var topProductsAdapter: TopProductsAdapter
    private lateinit var btnToday: MaterialButton
    private lateinit var btnWeek: MaterialButton
    private lateinit var btnMonth: MaterialButton

    private var selectedTimeRange: TimeRange = TimeRange.TODAY

    enum class TimeRange {
        TODAY, WEEK, MONTH
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sales_report, container, false)
        
        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(requireContext()).coffeeShopDao())
        
        // Initialize views
        totalRevenueText = view.findViewById(R.id.total_revenue)
        totalOrdersText = view.findViewById(R.id.total_orders)
        avgOrderValueText = view.findViewById(R.id.avg_order_value)
        salesChart = view.findViewById(R.id.sales_chart)
        topProductsRecycler = view.findViewById(R.id.top_products_recycler)
        btnToday = view.findViewById(R.id.btn_today)
        btnWeek = view.findViewById(R.id.btn_week)
        btnMonth = view.findViewById(R.id.btn_month)

        // Setup RecyclerView
        topProductsAdapter = TopProductsAdapter()
        topProductsRecycler.layoutManager = LinearLayoutManager(requireContext())
        topProductsRecycler.adapter = topProductsAdapter

        // Setup chart
        setupChart()

        // Setup time range buttons
        setupTimeRangeButtons()

        // Load initial data
        loadSalesData()

        return view
    }

    private fun setupChart() {
        salesChart.description.isEnabled = false
        salesChart.setTouchEnabled(true)
        salesChart.isDragEnabled = true
        salesChart.setScaleEnabled(true)
        salesChart.setPinchZoom(true)

        val xAxis = salesChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        val yAxis = salesChart.axisLeft
        yAxis.setDrawGridLines(true)
        yAxis.axisMinimum = 0f

        salesChart.axisRight.isEnabled = false
    }

    private fun setupTimeRangeButtons() {
        btnToday.setOnClickListener {
            selectedTimeRange = TimeRange.TODAY
            updateButtonStates()
            loadSalesData()
        }

        btnWeek.setOnClickListener {
            selectedTimeRange = TimeRange.WEEK
            updateButtonStates()
            loadSalesData()
        }

        btnMonth.setOnClickListener {
            selectedTimeRange = TimeRange.MONTH
            updateButtonStates()
            loadSalesData()
        }

        updateButtonStates()
    }

    private fun updateButtonStates() {
        btnToday.isSelected = selectedTimeRange == TimeRange.TODAY
        btnWeek.isSelected = selectedTimeRange == TimeRange.WEEK
        btnMonth.isSelected = selectedTimeRange == TimeRange.MONTH
    }

    private fun loadSalesData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val orders = repository.getAllOrders()
                val filteredOrders = filterOrdersByTimeRange(orders)
                
                // Calculate statistics
                val totalRevenue = filteredOrders.sumOf { it.total }
                val totalOrders = filteredOrders.size
                val avgOrderValue = if (totalOrders > 0) totalRevenue / totalOrders else 0.0

                // Update UI
                totalRevenueText.text = String.format("$%.2f", totalRevenue)
                totalOrdersText.text = totalOrders.toString()
                avgOrderValueText.text = String.format("$%.2f", avgOrderValue)

                // Update chart
                updateChart(filteredOrders)

                // Update top products
                updateTopProducts(filteredOrders)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading sales data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterOrdersByTimeRange(orders: List<Order>): List<Order> {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        
        return when (selectedTimeRange) {
            TimeRange.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                val startOfDay = calendar.timeInMillis
                orders.filter { it.timestamp in startOfDay..now }
            }
            TimeRange.WEEK -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val startOfWeek = calendar.timeInMillis
                orders.filter { it.timestamp in startOfWeek..now }
            }
            TimeRange.MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                val startOfMonth = calendar.timeInMillis
                orders.filter { it.timestamp in startOfMonth..now }
            }
        }
    }

    private fun updateChart(orders: List<Order>) {
        val entries = mutableListOf<Entry>()
        val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        
        // Group orders by date
        val ordersByDate = orders.groupBy { 
            dateFormat.format(Date(it.timestamp))
        }.toSortedMap()

        var index = 0f
        ordersByDate.forEach { (date, dateOrders) ->
            val total = dateOrders.sumOf { it.total }
            entries.add(Entry(index++, total.toFloat()))
        }

        val dataSet = LineDataSet(entries, "Daily Sales")
        dataSet.color = resources.getColor(R.color.purple, null)
        dataSet.valueTextColor = resources.getColor(R.color.black, null)
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(true)
        dataSet.setDrawValues(true)

        val lineData = LineData(dataSet)
        salesChart.data = lineData
        salesChart.invalidate()
    }

    private fun updateTopProducts(orders: List<Order>) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val allProducts = repository.getAllProducts()
                val productSales = mutableMapOf<Long, Int>()

                // Count product sales
                orders.forEach { order ->
                    val orderItems = repository.getItemsForOrder(order.orderId)
                    orderItems.forEach { item ->
                        productSales[item.productId] = productSales.getOrDefault(item.productId, 0) + item.quantity
                    }
                }

                // Create list of top products
                val topProducts = productSales.entries
                    .sortedByDescending { it.value }
                    .take(5)
                    .mapNotNull { entry ->
                        allProducts.find { it.id == entry.key }?.let { product ->
                            TopProduct(product, entry.value)
                        }
                    }

                topProductsAdapter.updateProducts(topProducts)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading top products: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

data class TopProduct(
    val product: Product,
    val quantitySold: Int
)

class TopProductsAdapter : RecyclerView.Adapter<TopProductsAdapter.ViewHolder>() {
    private var products = listOf<TopProduct>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.product_name)
        val quantityText: TextView = view.findViewById(R.id.quantity_sold)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.nameText.text = product.product.name
        holder.quantityText.text = "Sold: ${product.quantitySold}"
    }

    override fun getItemCount() = products.size

    fun updateProducts(newProducts: List<TopProduct>) {
        products = newProducts
        notifyDataSetChanged()
    }
} 