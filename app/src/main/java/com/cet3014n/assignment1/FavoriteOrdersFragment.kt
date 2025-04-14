package com.cet3014n.assignment1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteOrdersFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteOrdersAdapter
    private lateinit var repository: CoffeeShopRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_orders, container, false)
        
        // Initialize views
        recyclerView = view.findViewById(R.id.favorite_orders_recycler)
        
        // Initialize repository
        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(requireContext()).coffeeShopDao())
        
        // Setup RecyclerView
        adapter = FavoriteOrdersAdapter(
            onReorderClick = { favoriteOrder ->
                reorderFavorite(favoriteOrder)
            },
            onDeleteClick = { favoriteOrder ->
                deleteFavorite(favoriteOrder)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFavoriteOrders()
    }

    private fun loadFavoriteOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userEmail = sharedPreferences.getString("loggedInUserEmail", null)
                
                if (userEmail == null) {
                    Toast.makeText(requireContext(), "Please log in to view favorite orders", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                val user = repository.getUserByEmail(userEmail)
                if (user == null) {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // Use repeatOnLifecycle for Flow collection
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    try {
                        repository.getFavoriteOrders(user.id).collect { favoriteOrders ->
                            Log.d("FavoriteOrdersFragment", "Received ${favoriteOrders.size} favorite orders")
                            adapter.updateFavoriteOrders(favoriteOrders)
                        }
                    } catch (e: Exception) {
                        if (e is kotlinx.coroutines.CancellationException) {
                            // Ignore cancellation exceptions as they are expected
                            return@repeatOnLifecycle
                        }
                        Log.e("FavoriteOrdersFragment", "Error collecting favorite orders", e)
                        Toast.makeText(requireContext(), "Error loading favorite orders: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("FavoriteOrdersFragment", "Error loading favorite orders", e)
                Toast.makeText(requireContext(), "Error loading favorite orders: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reorderFavorite(favoriteOrder: FavoriteOrder) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val (_, items) = repository.getFavoriteOrderWithItems(favoriteOrder.id)
                
                // Clear current cart
                CartManager.clearCart()
                
                // Add items to cart
                for ((product, quantity) in items) {
                    CartManager.addItem(product, quantity)
                }
                
                // Navigate to cart
                val intent = Intent(requireContext(), CartActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("FavoriteOrdersFragment", "Error reordering favorite", e)
                Toast.makeText(requireContext(), "Error reordering favorite: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteFavorite(favoriteOrder: FavoriteOrder) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                repository.deleteFavoriteOrder(favoriteOrder)
                Toast.makeText(requireContext(), "Favorite order deleted", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("FavoriteOrdersFragment", "Error deleting favorite order", e)
                Toast.makeText(requireContext(), "Error deleting favorite order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 