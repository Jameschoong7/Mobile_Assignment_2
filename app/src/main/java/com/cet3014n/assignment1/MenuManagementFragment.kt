package com.cet3014n.assignment1

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MenuManagementFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuManagementAdapter
    private lateinit var repository: CoffeeShopRepository
    private lateinit var addItemButton: Button

    private val categories = arrayOf("Coffee", "Tea", "Pastries")
    private val dietaryOptions = listOf("Vegetarian", "Gluten-Free")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu_management, container, false)
        
        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(requireContext()).coffeeShopDao())
        
        recyclerView = view.findViewById(R.id.menu_items_recycler)
        addItemButton = view.findViewById(R.id.add_item_button)
        
        adapter = MenuManagementAdapter(
            onEditClick = { product -> showEditDialog(product) },
            onDeleteClick = { product -> showDeleteConfirmation(product) }
        )
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        
        addItemButton.setOnClickListener {
            showAddDialog()
        }
        
        loadMenuItems()
        
        return view
    }

    private fun loadMenuItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val products = repository.getAllProducts()
                adapter.updateProducts(products)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading menu items: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_menu_item, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.name_input)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.description_input)
        val priceInput = dialogView.findViewById<EditText>(R.id.price_input)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.category_spinner)
        val vegetarianCheckbox = dialogView.findViewById<CheckBox>(R.id.vegetarian_checkbox)
        val glutenFreeCheckbox = dialogView.findViewById<CheckBox>(R.id.gluten_free_checkbox)

        // Setup category spinner
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        AlertDialog.Builder(requireContext())
            .setTitle("Add Menu Item")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()
                val price = priceInput.text.toString().toDoubleOrNull()
                val category = categorySpinner.selectedItem.toString()
                val dietary = mutableListOf<String>().apply {
                    if (vegetarianCheckbox.isChecked) add("Vegetarian")
                    if (glutenFreeCheckbox.isChecked) add("Gluten-Free")
                }

                if (name.isEmpty() || description.isEmpty() || price == null) {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val product = Product(
                            name = name,
                            description = description,
                            price = price,
                            category = category,
                            dietary = dietary,
                            imageResId = R.drawable.coffee_placeholder // Default image
                        )
                        repository.insertProduct(product)
                        loadMenuItems()
                        Toast.makeText(requireContext(), "Menu item added successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error adding menu item: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(product: Product) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_menu_item, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.name_input)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.description_input)
        val priceInput = dialogView.findViewById<EditText>(R.id.price_input)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.category_spinner)
        val vegetarianCheckbox = dialogView.findViewById<CheckBox>(R.id.vegetarian_checkbox)
        val glutenFreeCheckbox = dialogView.findViewById<CheckBox>(R.id.gluten_free_checkbox)

        // Setup category spinner
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        // Set current values
        nameInput.setText(product.name)
        descriptionInput.setText(product.description)
        priceInput.setText(product.price.toString())
        categorySpinner.setSelection(categories.indexOf(product.category))
        vegetarianCheckbox.isChecked = "Vegetarian" in product.dietary
        glutenFreeCheckbox.isChecked = "Gluten-Free" in product.dietary

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Menu Item")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()
                val price = priceInput.text.toString().toDoubleOrNull()
                val category = categorySpinner.selectedItem.toString()
                val dietary = mutableListOf<String>().apply {
                    if (vegetarianCheckbox.isChecked) add("Vegetarian")
                    if (glutenFreeCheckbox.isChecked) add("Gluten-Free")
                }

                if (name.isEmpty() || description.isEmpty() || price == null) {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val updatedProduct = product.copy(
                            name = name,
                            description = description,
                            price = price,
                            category = category,
                            dietary = dietary
                        )
                        repository.updateProduct(updatedProduct)
                        loadMenuItems()
                        Toast.makeText(requireContext(), "Menu item updated successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error updating menu item: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Menu Item")
            .setMessage("Are you sure you want to delete ${product.name}?")
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        repository.deleteProduct(product)
                        loadMenuItems()
                        Toast.makeText(requireContext(), "Menu item deleted successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error deleting menu item: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
} 