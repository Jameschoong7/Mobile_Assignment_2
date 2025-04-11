package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    companion object {
        private const val TAG = "CafeMenuFragment"
    }

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
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Starting view initialization")

        // Initialize views
        recyclerView = view.findViewById(R.id.menu_recycler)
        emptyMenuMessage = view.findViewById(R.id.empty_menu_message)
        categorySelectionLayout = view.findViewById(R.id.category_selection_layout)
        menuBrowsingLayout = view.findViewById(R.id.menu_browsing_layout)
        categoryLabel = view.findViewById(R.id.category_label)
        dietaryFilterGroup = view.findViewById(R.id.dietary_filter_group)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        Log.d(TAG, "onViewCreated: Views initialized")

        // Initialize repository
        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(requireContext()).coffeeShopDao())

        // Set up button listeners
        setCategoryButtonListeners()

        // Load menu items
        loadMenuFromDatabase()
    }

    private fun setCategoryButtonListeners() {
        Log.d(TAG, "setCategoryButtonListeners: Setting up listeners")
        
        val coffeeButton = view?.findViewById<LinearLayout>(R.id.category_coffee_button)
        val teaButton = view?.findViewById<LinearLayout>(R.id.category_tea_button)
        val pastriesButton = view?.findViewById<LinearLayout>(R.id.category_pastries_button)
        val backButton = view?.findViewById<Button>(R.id.back_to_categories_button)

        Log.d(TAG, "setCategoryButtonListeners: Buttons found - Coffee: $coffeeButton, Tea: $teaButton, Pastries: $pastriesButton, Back: $backButton")

        coffeeButton?.setOnClickListener {
            Log.d(TAG, "Coffee button clicked")
            currentCategoryFilter = "Coffee"
            categoryLabel.text = "Category: Coffee"
            categorySelectionLayout.visibility = View.GONE
            menuBrowsingLayout.visibility = View.VISIBLE
            applyFilters()
        }

        teaButton?.setOnClickListener {
            Log.d(TAG, "Tea button clicked")
            currentCategoryFilter = "Tea"
            categoryLabel.text = "Category: Tea"
            categorySelectionLayout.visibility = View.GONE
            menuBrowsingLayout.visibility = View.VISIBLE
            applyFilters()
        }

        pastriesButton?.setOnClickListener {
            Log.d(TAG, "Pastries button clicked")
            currentCategoryFilter = "Pastries"
            categoryLabel.text = "Category: Pastries"
            categorySelectionLayout.visibility = View.GONE
            menuBrowsingLayout.visibility = View.VISIBLE
            applyFilters()
        }

        backButton?.setOnClickListener {
            Log.d(TAG, "Back button clicked")
            currentCategoryFilter = null
            categorySelectionLayout.visibility = View.VISIBLE
            menuBrowsingLayout.visibility = View.GONE
        }
    }

    private fun loadMenuFromDatabase() {
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

