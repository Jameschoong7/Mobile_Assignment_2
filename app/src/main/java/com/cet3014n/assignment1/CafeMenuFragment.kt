package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
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
    private lateinit var trackOrderButton:Button
    private lateinit var dietaryFilterGroup: RadioGroup
    private lateinit var cartIcon: ImageView
    private lateinit var searchEditText: EditText
    private var currentCategoryFilter: String? = null
    private var currentDietaryFilter: String = "All Dietary"
    private var currentSearchQuery: String = ""

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
        cartIcon = view.findViewById(R.id.cart_icon)
        trackOrderButton = view.findViewById(R.id.track_orders_button)
        searchEditText = view.findViewById(R.id.search_edit_text)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        Log.d(TAG, "onViewCreated: Views initialized")

        // Initialize repository
        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(requireContext()).coffeeShopDao())

        // Set up button listeners
        setCategoryButtonListeners()
        setupCartIcon()
        setupDietaryFilter()
        setupSearch()

        trackOrderButton.setOnClickListener {
            val intent = Intent(requireActivity(), OrderTrackingActivity::class.java)
            startActivity(intent)
        }
        // Load menu items
        loadMenuFromDatabase()
    }

    private fun setCategoryButtonListeners() {
        Log.d(TAG, "setCategoryButtonListeners: Setting up listeners")
        
        val allItemsButton = view?.findViewById<LinearLayout>(R.id.category_all_button)
        val coffeeButton = view?.findViewById<LinearLayout>(R.id.category_coffee_button)
        val teaButton = view?.findViewById<LinearLayout>(R.id.category_tea_button)
        val pastriesButton = view?.findViewById<LinearLayout>(R.id.category_pastries_button)
        val backButton = view?.findViewById<Button>(R.id.back_to_categories_button)

        Log.d(TAG, "setCategoryButtonListeners: Buttons found - All Items: $allItemsButton, Coffee: $coffeeButton, Tea: $teaButton, Pastries: $pastriesButton, Back: $backButton")

        allItemsButton?.setOnClickListener {
            Log.d(TAG, "All Items button clicked")
            currentCategoryFilter = null
            categoryLabel.text = "Category: All Items"
            categorySelectionLayout.visibility = View.GONE
            menuBrowsingLayout.visibility = View.VISIBLE
            applyFilters()
        }

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

    private fun setupCartIcon() {
        cartIcon.setOnClickListener {
            Log.d(TAG, "Cart icon clicked")
            val intent = Intent(requireContext(), CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupDietaryFilter() {
        dietaryFilterGroup.setOnCheckedChangeListener { group, checkedId ->
            currentDietaryFilter = when (checkedId) {
                R.id.dietary_all -> "All Dietary"
                R.id.dietary_gluten_free -> "Gluten-Free"
                R.id.dietary_vegetarian -> "Vegetarian"
                else -> "All Dietary"
            }
            applyFilters()
        }
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                currentSearchQuery = s.toString().trim()
                applyFilters()
            }
        })
    }

    private fun loadMenuFromDatabase() {
        lifecycleScope.launch {
            val allProducts = repository.getAllProducts()
            allMenuItems = allProducts
            applyFilters()
        }
    }

    private fun applyFilters() {
        val categoryFilteredItems = if (currentCategoryFilter == null) {
            allMenuItems
        } else {
            allMenuItems.filter { it.category == currentCategoryFilter }
        }

        val dietaryFilteredItems = when (currentDietaryFilter) {
            "All Dietary" -> categoryFilteredItems
            else -> categoryFilteredItems.filter { product -> 
                product.dietary.any { it == currentDietaryFilter }
            }
        }

        val searchFilteredItems = if (currentSearchQuery.isNotEmpty()) {
            dietaryFilteredItems.filter { product ->
                product.name.contains(currentSearchQuery, ignoreCase = true) ||
                product.description.contains(currentSearchQuery, ignoreCase = true)
            }
        } else {
            dietaryFilteredItems
        }

        if (searchFilteredItems.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyMenuMessage.visibility = View.VISIBLE
            emptyMenuMessage.text = "No items match your search."
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyMenuMessage.visibility = View.GONE
            menuAdapter = MenuAdapter(searchFilteredItems)
            recyclerView.adapter = menuAdapter
        }
    }
}

