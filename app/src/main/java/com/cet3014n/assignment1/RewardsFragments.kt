package com.cet3014n.assignment1

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RewardsFragment : Fragment() {
    private lateinit var pointsBalanceText: TextView
    private lateinit var redeemButton: Button
    private lateinit var transactionsRecycler: RecyclerView
    private lateinit var transactionAdapter: RewardTransactionAdapter
    private lateinit var repository: CoffeeShopRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rewards, container, false)
        Log.d("RewardsFragment", "onCreateView called")

        // Initialize views
        pointsBalanceText = view.findViewById(R.id.points_balance_text)
        redeemButton = view.findViewById(R.id.redeem_button)
        transactionsRecycler = view.findViewById(R.id.transactions_recycler)

        // Initialize repository
        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(requireContext()).coffeeShopDao())

        // Setup RecyclerView
        transactionAdapter = RewardTransactionAdapter()
        transactionsRecycler.layoutManager = LinearLayoutManager(requireContext())
        transactionsRecycler.adapter = transactionAdapter

        // Load user data and transactions
        loadUserData()
        loadTransactions()

        // Setup redeem button
        redeemButton.setOnClickListener {
            Log.d("RewardsFragment", "Redeem button clicked")
            showRedeemDialog()
        }

        return view
    }

    private fun loadUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("RewardsFragment", "Loading user data")
                // Get current user's email from SharedPreferences
                val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userEmail = sharedPreferences.getString("loggedInUserEmail", null)
                Log.d("RewardsFragment", "User email from SharedPreferences: $userEmail")

                if (userEmail == null) {
                    pointsBalanceText.text = "Please log in to view rewards"
                    redeemButton.isEnabled = false
                    redeemButton.alpha = 0.5f // Make button appear greyed out
                    return@launch
                }

                // Get user from database
                val user = repository.getUserByEmail(userEmail)
                Log.d("RewardsFragment", "User from database: $user")

                if (user == null) {
                    pointsBalanceText.text = "User not found"
                    redeemButton.isEnabled = false
                    redeemButton.alpha = 0.5f // Make button appear greyed out
                    return@launch
                }

                // Update points display
                pointsBalanceText.text = "Points Balance: ${user.loyaltyPoints} (RM ${String.format("%.2f", user.loyaltyPoints * 0.01)})"
                
                // Update redeem button state
                if (user.loyaltyPoints > 0) {
                    redeemButton.isEnabled = true
                    redeemButton.alpha = 1.0f // Make button fully visible
                } else {
                    redeemButton.isEnabled = false
                    redeemButton.alpha = 0.5f // Make button appear greyed out
                }
                
                Log.d("RewardsFragment", "Points balance updated: ${user.loyaltyPoints}")
            } catch (e: Exception) {
                Log.e("RewardsFragment", "Error loading user data", e)
                Toast.makeText(requireContext(), "Error loading user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("RewardsFragment", "Loading transactions")
                // Get current user's email from SharedPreferences
                val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userEmail = sharedPreferences.getString("loggedInUserEmail", null)
                Log.d("RewardsFragment", "User email for transactions: $userEmail")

                if (userEmail == null) {
                    Log.d("RewardsFragment", "No user email found")
                    return@launch
                }

                // Get user from database
                val user = repository.getUserByEmail(userEmail)
                Log.d("RewardsFragment", "User for transactions: $user")
                
                if (user == null) {
                    Log.d("RewardsFragment", "No user found")
                    return@launch
                }

                // Collect transactions
                Log.d("RewardsFragment", "Starting to collect transactions for user ${user.id}")
                repository.getRewardTransactions(user.id).collect { transactions ->
                    Log.d("RewardsFragment", "Received ${transactions.size} transactions")
                    if (transactions.isEmpty()) {
                        Log.d("RewardsFragment", "No transactions found")
                    } else {
                        transactions.forEach { transaction ->
                            Log.d("RewardsFragment", "Transaction: $transaction")
                        }
                    }
                    transactionAdapter.updateTransactions(transactions)
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    Log.d("RewardsFragment", "Transaction loading cancelled")
                } else {
                    Log.e("RewardsFragment", "Error loading transactions", e)
                    Toast.makeText(requireContext(), "Error loading transactions: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showRedeemDialog() {
        Log.d("RewardsFragment", "showRedeemDialog called")
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Get current user's email from SharedPreferences
                val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userEmail = sharedPreferences.getString("loggedInUserEmail", null)
                Log.d("RewardsFragment", "User email for redeem: $userEmail")

                if (userEmail == null) {
                    Toast.makeText(requireContext(), "Please log in to redeem points", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Get user from database
                val user = repository.getUserByEmail(userEmail)
                Log.d("RewardsFragment", "User for redeem: $user")

                if (user == null) {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Create dialog
                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_redeem_points, null)
                val pointsInput = dialogView.findViewById<android.widget.EditText>(R.id.points_input)
                val maxPointsText = dialogView.findViewById<TextView>(R.id.max_points_text)
                maxPointsText.text = "Maximum points: ${user.loyaltyPoints} (RM ${String.format("%.2f", user.loyaltyPoints * 0.01)})"

                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("Redeem Points")
                    .setView(dialogView)
                    .setPositiveButton("Redeem") { _, _ ->
                        val pointsToRedeem = pointsInput.text.toString().toIntOrNull()
                        Log.d("RewardsFragment", "Points to redeem: $pointsToRedeem")

                        if (pointsToRedeem == null || pointsToRedeem <= 0) {
                            Toast.makeText(requireContext(), "Please enter a valid number of points", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        if (pointsToRedeem > user.loyaltyPoints) {
                            Toast.makeText(requireContext(), "Not enough points", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        // Calculate discount (1 point = RM 0.01)
                        val discount = pointsToRedeem * 0.01
                        Log.d("RewardsFragment", "Calculated discount: RM $discount")

                        // Store the redeemed discount in SharedPreferences
                        val sharedPrefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        sharedPrefs.edit().apply {
                            putFloat("redeemedDiscount", discount.toFloat())
                            apply()
                        }
                        Log.d("RewardsFragment", "Stored discount in SharedPreferences: RM $discount")

                        // Create reward transaction
                        val rewardTransaction = RewardTransaction(
                            userId = user.id,
                            points = -pointsToRedeem, // Negative points for redemption
                            type = TransactionType.REDEEMED,
                            description = "Points redeemed for RM ${String.format("%.2f", discount)} discount"
                        )
                        Log.d("RewardsFragment", "Created reward transaction: $rewardTransaction")

                        // Update user's loyalty points
                        val updatedUser = user.copy(loyaltyPoints = user.loyaltyPoints - pointsToRedeem)
                        Log.d("RewardsFragment", "Updated user points: ${user.loyaltyPoints} -> ${updatedUser.loyaltyPoints}")

                        viewLifecycleOwner.lifecycleScope.launch {
                            try {
                                Log.d("RewardsFragment", "Inserting reward transaction: $rewardTransaction")
                                repository.insertRewardTransaction(rewardTransaction)
                                Log.d("RewardsFragment", "Updating user: $updatedUser")
                                repository.updateUser(updatedUser)
                                Toast.makeText(requireContext(), "Points redeemed successfully! RM ${String.format("%.2f", discount)} discount will be applied to your next order.", Toast.LENGTH_LONG).show()
                                loadUserData() // Refresh points display
                                loadTransactions() // Refresh transaction list
                            } catch (e: Exception) {
                                Log.e("RewardsFragment", "Error redeeming points", e)
                                Toast.makeText(requireContext(), "Error redeeming points: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .create()

                dialog.show()
            } catch (e: Exception) {
                Log.e("RewardsFragment", "Error showing redeem dialog", e)
                Toast.makeText(requireContext(), "Error showing redeem dialog: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}