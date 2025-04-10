package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.InputStream

class CafeMenuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var repository: CoffeeShopRepository
    private lateinit var allMenuItems: List<Product>
    private lateinit var emptyMenuMessage: TextView
    private lateinit var categorySelectionLayout: LinearLayout
    private lateinit var menuBrowsingLayout: LinearLayout
    private lateinit var categoryLabel: TextView
    private lateinit var dietaryFilterGroup: RadioGroup
    private var currentCategoryFilter: String? = null
    private var currentDietaryFilter: String = "All Dietary"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        recyclerView = view.findViewById(R.id.menu_recycler)
        emptyMenuMessage = view.findViewById(R.id.empty_menu_message)
        categorySelectionLayout = view.findViewById(R.id.category_selection_layout)
        menuBrowsingLayout = view.findViewById(R.id.menu_browsing_layout)
        categoryLabel = view.findViewById(R.id.category_label)
        dietaryFilterGroup = view.findViewById(R.id.dietary_filter_group)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(requireContext()).coffeeShopDao())
        // Load menu items from Room
        loadMenuFromDatabase()

        return view
    }

    private fun loadMenuFromDatabase() {
        // Use a coroutine or lifecycle scope to run database queries on a background thread
        lifecycleScope.launch {
            val allProducts = repository.getAllProducts()
            allMenuItems = allProducts
            applyFilters()
        }
    }

    private fun applyFilters() {
        if (currentCategoryFilter == null) return

        val categoryFilteredItems = allMenuItems.filter { it.category == currentCategoryFilter }

        val filteredItems = when (currentDietaryFilter) {
            "All Dietary" -> categoryFilteredItems
            else -> categoryFilteredItems.filter { currentDietaryFilter in it.dietary }
        }

        if (filteredItems.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyMenuMessage.visibility = View.VISIBLE
            emptyMenuMessage.text = "No items available."
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyMenuMessage.visibility = View.GONE
            menuAdapter = MenuAdapter(filteredItems)
            recyclerView.adapter = menuAdapter
        }
    }
}
