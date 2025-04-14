package com.cet3014n.assignment1

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

class CustomerSupportManagementFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomerSupportAdapter
    private lateinit var repository: CoffeeShopRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customer_support_management, container, false)
        
        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(requireContext()).coffeeShopDao())
        
        recyclerView = view.findViewById(R.id.feedback_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = CustomerSupportAdapter()
        recyclerView.adapter = adapter
        
        loadFeedback()
        
        return view
    }

    private fun loadFeedback() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Get all users
                val users = repository.getAllUsers()
                val feedbackList = mutableListOf<Pair<SupportFeedback, User>>()
                
                // For each user, get their feedback
                for (user in users) {
                    val feedback = repository.getSupportFeedback(user.id)
                    feedback.forEach { fb ->
                        feedbackList.add(Pair(fb, user))
                    }
                }
                
                // Sort by timestamp (newest first)
                feedbackList.sortByDescending { it.first.timestamp }
                
                adapter.updateFeedback(feedbackList)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading feedback: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 