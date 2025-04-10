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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.io.InputStream

class CafeMenuFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var allMenuItems: List<CoffeeMenuItem>
    private lateinit var emptyMenuMessage: TextView
    private lateinit var categorySelectionLayout: LinearLayout
    private lateinit var menuBrowsingLayout: LinearLayout
    private lateinit var categoryLabel: TextView
    private lateinit var dietaryFilterGroup: RadioGroup
    private var currentCategoryFilter: String? = null // Initially null until a category is selected
    private var currentDietaryFilter: String = "All Dietary"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable options menu for this fragment
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

        // Load menu items from JSON
        allMenuItems = loadMenuFromJson()

        // Restore state if available
        savedInstanceState?.let {
            currentCategoryFilter = it.getString("currentCategoryFilter")
            currentDietaryFilter = it.getString("currentDietaryFilter", "All Dietary")
            val selectedDietaryId = when (currentDietaryFilter) {
                "Gluten-Free" -> R.id.dietary_gluten_free
                "Vegetarian" -> R.id.dietary_vegetarian
                else -> R.id.dietary_all
            }
            dietaryFilterGroup.check(selectedDietaryId)
        }

        // Show category selection screen initially
        if (currentCategoryFilter == null) {
            categorySelectionLayout.visibility = View.VISIBLE
            menuBrowsingLayout.visibility = View.GONE
        } else {
            categorySelectionLayout.visibility = View.GONE
            menuBrowsingLayout.visibility = View.VISIBLE
            categoryLabel.text = "Category: $currentCategoryFilter"
            applyFilters()
        }

        // Handle category selection
        view.findViewById<LinearLayout>(R.id.category_coffee_button).setOnClickListener {
            currentCategoryFilter = "Coffee"
            categoryLabel.text = "Category: Coffee"
            categorySelectionLayout.visibility = View.GONE
            menuBrowsingLayout.visibility = View.VISIBLE
            applyFilters()
        }

        view.findViewById<LinearLayout>(R.id.category_tea_button).setOnClickListener {
            currentCategoryFilter = "Tea"
            categoryLabel.text = "Category: Tea"
            categorySelectionLayout.visibility = View.GONE
            menuBrowsingLayout.visibility = View.VISIBLE
            applyFilters()
        }

        view.findViewById<LinearLayout>(R.id.category_pastries_button).setOnClickListener {
            currentCategoryFilter = "Pastries"
            categoryLabel.text = "Category: Pastries"
            categorySelectionLayout.visibility = View.GONE
            menuBrowsingLayout.visibility = View.VISIBLE
            applyFilters()
        }

        view.findViewById<Button>(R.id.back_to_categories_button).setOnClickListener {
            currentCategoryFilter = null
            categorySelectionLayout.visibility = View.VISIBLE
            menuBrowsingLayout.visibility = View.GONE
        }

        // Handle dietary filter changes
        dietaryFilterGroup.setOnCheckedChangeListener { _, checkedId ->
            currentDietaryFilter = when (checkedId) {
                R.id.dietary_gluten_free -> "Gluten-Free"
                R.id.dietary_vegetarian -> "Vegetarian"
                else -> "All Dietary"
            }
            applyFilters()
        }

        val cartIcon = view.findViewById<ImageView>(R.id.cart_icon)
        cartIcon.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }

        val trackOrdersButton = view.findViewById<Button>(R.id.track_orders_button)
        trackOrdersButton.setOnClickListener {
            startActivity(Intent(requireContext(), OrderTrackingActivity::class.java))
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                startActivity(Intent(requireActivity(), LogoutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun applyFilters() {
        if (currentCategoryFilter == null) return // Don't apply filters until a category is selected

        val categoryFilteredItems = allMenuItems.filter { it.category == currentCategoryFilter }

        val filteredItems = when (currentDietaryFilter) {
            "All Dietary" -> categoryFilteredItems
            else -> categoryFilteredItems.filter { currentDietaryFilter in it.dietary }
        }

        if (filteredItems.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyMenuMessage.visibility = View.VISIBLE
            emptyMenuMessage.text = if (categoryFilteredItems.isEmpty()) {
                "No items available in this category."
            } else {
                "No items match the selected dietary filter."
            }
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyMenuMessage.visibility = View.GONE
            menuAdapter = MenuAdapter(filteredItems)
            recyclerView.adapter = menuAdapter
        }
    }

    private fun loadMenuFromJson(): List<CoffeeMenuItem> {
        return try {
            val inputStream: InputStream = resources.openRawResource(R.raw.menu)
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)
            val menuItems = mutableListOf<CoffeeMenuItem>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val imageName = jsonObject.getString("image")
                val imageResId = try {
                    resources.getIdentifier(imageName, "drawable", requireContext().packageName).takeIf { it != 0 }
                        ?: R.mipmap.ic_launcher
                } catch (e: Exception) {
                    R.mipmap.ic_launcher
                }
                val dietaryArray = jsonObject.getJSONArray("dietary")
                val dietaryList = mutableListOf<String>()
                for (j in 0 until dietaryArray.length()) {
                    dietaryList.add(dietaryArray.getString(j))
                }
                menuItems.add(
                    CoffeeMenuItem(
                        name = jsonObject.getString("name"),
                        price = jsonObject.getDouble("price"),
                        description = jsonObject.getString("description"),
                        category = jsonObject.getString("category"),
                        dietary = dietaryList,
                        imageResId = imageResId
                    )
                )
            }
            menuItems
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentCategoryFilter", currentCategoryFilter)
        outState.putString("currentDietaryFilter", currentDietaryFilter)
    }
}