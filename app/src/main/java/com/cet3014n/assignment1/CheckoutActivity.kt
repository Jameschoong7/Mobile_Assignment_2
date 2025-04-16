package com.cet3014n.assignment1

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CheckoutActivity : AppCompatActivity() {
    private lateinit var totalAmountTextView: TextView
    private lateinit var paymentMethodGroup: RadioGroup
    private lateinit var cardNumberEditText: EditText
    private lateinit var expiryDateEditText: EditText
    private lateinit var cvvEditText: EditText
    private lateinit var savePaymentCheckBox: CheckBox
    private lateinit var payButton: Button
    private lateinit var paymentFormLayout: LinearLayout
    private lateinit var paymentConfirmationLayout: View
    private lateinit var receiptTextView: TextView
    private lateinit var trackOrderButton: Button
    private lateinit var backButton: Button
    private var isPaymentProcessed = false // Flag to track payment status
    private var subtotal: Double = 0.0
    private var total: Double = 0.0
    private var discount: Double = 0.0
    private var promoCode: String? = null
    private var deliveryAddress:String?=null
    private lateinit var repository: CoffeeShopRepository
    private lateinit var saveAsFavoriteCheckbox: CheckBox
    private lateinit var favoriteOrderNameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        supportActionBar?.apply {
            title = "Checkout"
        }

        // Initialize repository
        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(this).coffeeShopDao())

        totalAmountTextView = findViewById(R.id.total_amount)
        paymentMethodGroup = findViewById(R.id.payment_method_group)
        cardNumberEditText = findViewById(R.id.card_number)
        expiryDateEditText = findViewById(R.id.expiry_date)
        cvvEditText = findViewById(R.id.cvv)
        savePaymentCheckBox = findViewById(R.id.save_payment_checkbox)
        payButton = findViewById(R.id.pay_button)
        paymentFormLayout = findViewById(R.id.payment_form_layout)
        paymentConfirmationLayout = findViewById(R.id.payment_confirmation_layout)
        receiptTextView = findViewById(R.id.receipt_text)
        trackOrderButton = findViewById(R.id.track_order_button)
        backButton = findViewById(R.id.back_button)
        saveAsFavoriteCheckbox = findViewById(R.id.save_as_favorite_checkbox)
        favoriteOrderNameInput = findViewById(R.id.favorite_order_name)

        // Handle back button click
        backButton.setOnClickListener {
            // Navigate directly to MainActivity with CafeMenuFragment
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragment", "cafeMenuFragment") // Instruct MainActivity to show CafeMenuFragment
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // Close CheckoutActivity
        }

        // Retrieve data from CartActivity
        val deliveryOption = intent.getStringExtra("deliveryOption") ?: "Pickup"
        deliveryAddress = intent.getStringExtra("deliveryAddress")
        subtotal = intent.getDoubleExtra("subtotal", 0.0)
        total = intent.getDoubleExtra("total", 0.0)
        promoCode = intent.getStringExtra("promoCode")
        discount = intent.getDoubleExtra("discount", 0.0)

        // Check for redeemed discount
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val redeemedDiscount = sharedPreferences.getFloat("redeemedDiscount", 0f).toDouble()
        
        // Apply redeemed discount if available
        if (redeemedDiscount > 0) {
            // Apply the redeemed discount to the total
            total = subtotal * (1 - discount) - redeemedDiscount

        }

        // Display the total with discount (if applied)
        totalAmountTextView.text = buildString {
            append("Subtotal: RM%.2f".format(subtotal))
            if (discount > 0 && promoCode != null) {
                append("\nDiscount ($promoCode): -RM%.2f".format(subtotal * discount))
            }
            if (redeemedDiscount > 0) {
                append("\nRedeemed Points Discount: -RM%.2f".format(redeemedDiscount))
            }
            append("\nTotal: RM%.2f".format(total))
            // Add points earning information
            val rewardPoints = maxOf(1, (total * 0.1).toInt())
            append("\n\nYou will earn: $rewardPoints points")
        }

        // Initially hide the confirmation layout
        paymentConfirmationLayout.visibility = View.GONE

        // Load saved payment details if they exist
        val savedCardNumber = sharedPreferences.getString("card_number", null)
        val savedExpiryDate = sharedPreferences.getString("expiry_date", null)
        val savedCvv = sharedPreferences.getString("cvv", null)

        if (savedCardNumber != null && savedExpiryDate != null && savedCvv != null) {
            cardNumberEditText.setText(savedCardNumber)
            expiryDateEditText.setText(savedExpiryDate)
            cvvEditText.setText(savedCvv)
            savePaymentCheckBox.isChecked = true
        }

        // Show/hide card input fields based on payment method
        paymentMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.payment_card -> {
                    paymentFormLayout.visibility = View.VISIBLE
                    payButton.visibility = View.VISIBLE
                }
                R.id.payment_wallet -> {
                    paymentFormLayout.visibility = View.GONE
                    payButton.visibility = View.VISIBLE
                }
            }
        }

        payButton.setOnClickListener {
            when (paymentMethodGroup.checkedRadioButtonId) {
                R.id.payment_card -> {
                    val cardNumber = cardNumberEditText.text.toString().trim()
                    val expiryDate = expiryDateEditText.text.toString().trim()
                    val cvv = cvvEditText.text.toString().trim()

                    if (cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
                        Toast.makeText(this, "Please fill in all payment details", Toast.LENGTH_SHORT).show()
                    } else {
                        // Save payment details if checkbox is ticked
                        if (savePaymentCheckBox.isChecked) {
                            val editor = sharedPreferences.edit()
                            editor.putString("card_number", cardNumber)
                            editor.putString("expiry_date", expiryDate)
                            editor.putString("cvv", cvv)
                            editor.apply()
                            Toast.makeText(this, "Payment details saved for future orders", Toast.LENGTH_SHORT).show()
                        } else {
                            // Clear saved payment details if checkbox is not ticked
                            val editor = sharedPreferences.edit()
                            editor.remove("card_number")
                            editor.remove("expiry_date")
                            editor.remove("cvv")
                            editor.apply()
                        }
                        isPaymentProcessed = true
                        simulatePaymentProcessing(cardNumber, deliveryOption, "Card")
                    }
                }
                R.id.payment_wallet -> {
                    // Link to Google Wallet URL as it is simulated
                    val googleWalletUrl = "https://wallet.google.com"
                    val uri = Uri.parse(googleWalletUrl)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            }
        }

        trackOrderButton.setOnClickListener {
            val intent = Intent(this, OrderTrackingActivity::class.java)
            intent.putExtra("total", total) // Pass the final total to OrderTrackingActivity
            startActivity(intent)
        }

        // Restore UI state if payment was already processed (like after screen rotation)
        if (isPaymentProcessed) {
            paymentFormLayout.visibility = View.GONE
            paymentMethodGroup.visibility = View.GONE
            findViewById<TextView>(R.id.payment_method_label).visibility = View.GONE
            payButton.visibility = View.GONE
            paymentConfirmationLayout.visibility = View.VISIBLE
        }

        // Setup checkbox listener
        saveAsFavoriteCheckbox.setOnCheckedChangeListener { _, isChecked ->
            favoriteOrderNameInput.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        // Check if returning from Google Wallet payment
        if (paymentMethodGroup.checkedRadioButtonId == R.id.payment_wallet && !isPaymentProcessed) {
            // Simulate successful payment after returning from Google Wallet
            isPaymentProcessed = true
            simulatePaymentProcessing("N/A", intent.getStringExtra("deliveryOption") ?: "Pickup", "Mobile Wallet")
        }
    }

    private fun simulatePaymentProcessing(cardNumber: String, deliveryOption: String, paymentMethod: String) {
        // Simulate payment success
        Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show()

        // Clear the redeemed discount after successful payment
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("redeemedDiscount").apply()

        // Create an order for tracking
        val orderId = "ORDER_${System.currentTimeMillis()}"
        val cartItems = CartManager.getItems().toList()
        Log.d("Cart Items",cartItems.toString())
        OrderManager.createOrder(orderId, cartItems, deliveryOption)

        // Update database
        lifecycleScope.launch {
            try {
                // Get current user's email from SharedPreferences
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userEmail = sharedPreferences.getString("loggedInUserEmail", null)
                
                if (userEmail == null) {
                    Toast.makeText(this@CheckoutActivity, "Please log in to place an order", Toast.LENGTH_LONG).show()
                    return@launch
                }

                // Get user from database
                val user = repository.getUserByEmail(userEmail)
                if (user == null) {
                    Toast.makeText(this@CheckoutActivity, "User not found", Toast.LENGTH_LONG).show()
                    return@launch
                }

                // Create order
                val order = Order(
                    orderId = orderId,
                    userId = user.id,
                    status = OrderStatus.PREPARING,
                    deliveryOption = deliveryOption,
                    deliveryAddress = deliveryAddress,
                    subtotal = subtotal,
                    total = total,
                    promoCode = promoCode,
                    discount = discount,
                    timestamp = System.currentTimeMillis()
                )
                repository.insertOrder(order)

                // Create order items
                for ((product, quantity) in cartItems) {
                    val orderItem = OrderItem(
                        orderId = orderId,
                        productId = product.id,
                        quantity = quantity
                    )
                    repository.insertOrderItem(orderItem)
                }

                // Calculate and add reward points (10% of total amount, minimum 1 point)
                val rewardPoints = maxOf(1, (total * 0.01).toInt())
                Log.d("CheckoutActivity", "Total amount: $total")
                Log.d("CheckoutActivity", "Calculated reward points: $rewardPoints")
                
                if (rewardPoints > 0) {
                    Log.d("CheckoutActivity", "Creating reward transaction for user ${user.id}")
                    // Create reward transaction
                    val rewardTransaction = RewardTransaction(
                        userId = user.id,
                        points = rewardPoints,
                        type = TransactionType.EARNED,
                        description = "Points earned from order #$orderId"
                    )
                    Log.d("CheckoutActivity", "Inserting reward transaction: $rewardTransaction")
                    repository.insertRewardTransaction(rewardTransaction)

                    // Update user's loyalty points
                    val updatedUser = user.copy(loyaltyPoints = user.loyaltyPoints + rewardPoints)
                    Log.d("CheckoutActivity", "Updating user points: ${user.loyaltyPoints} -> ${updatedUser.loyaltyPoints}")
                    repository.updateUser(updatedUser)
                    
                    Log.d("CheckoutActivity", "Reward points added successfully")
                } else {
                    Log.d("CheckoutActivity", "No reward points to add (rewardPoints <= 0)")
                }

                // After successful payment, check if we should save as favorite
                if (saveAsFavoriteCheckbox.isChecked) {
                    val name = favoriteOrderNameInput.text.toString().trim()
                    if (name.isEmpty()) {
                        Toast.makeText(this@CheckoutActivity, "Please enter a name for your favorite order", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    
                    saveAsFavoriteOrder(name, cartItems)
                }

                // Generate receipt after saving favorite order
                val receipt = buildString {
                    append("----- Payment Receipt -----\n")
                    append("Date: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(java.util.Date())}\n\n")
                    append("Items:\n")
                    cartItems.forEach { (item, quantity) ->
                        append("${item.name} (x$quantity): RM%.2f\n".format(item.price * quantity))
                    }
                    append("\nSubtotal: RM%.2f\n".format(subtotal))
                    if (discount > 0 && promoCode != null) {
                        append("Discount ($promoCode): -RM%.2f\n".format(subtotal * discount))
                    }
                    append("Total: RM%.2f\n".format(total))
                    append("Payment Method: $paymentMethod\n")
                    if (paymentMethod == "Card") {
                        append("Card ending in: ${cardNumber.takeLast(4)}\n")
                    }
                    append("Delivery Option: $deliveryOption\n")
                    append("--------------------------")
                }

                // Update UI on the main thread
                runOnUiThread {
                    // Show confirmation and receipt
                    paymentConfirmationLayout.visibility = View.VISIBLE
                    receiptTextView.text = receipt

                    // Hide payment form and related UI elements
                    paymentFormLayout.visibility = View.GONE
                    paymentMethodGroup.visibility = View.GONE
                    findViewById<TextView>(R.id.payment_method_label).visibility = View.GONE
                    payButton.visibility = View.GONE

                    // Show payment confirmation
                    showPaymentConfirmation(orderId, total)
                }

                // Clear cart only after everything is done
                CartManager.clearCart()
            } catch (e: Exception) {
                Toast.makeText(this@CheckoutActivity, "Error processing order: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showPaymentConfirmation(orderId: String, total: Double) {
        val rewardPoints = maxOf(1, (total * 0.1).toInt())
        val receiptText = buildString {
            append("Order ID: $orderId\n")
            append("Total Paid: RM ${String.format("%.2f", total)}\n")
            append("Points Earned: $rewardPoints\n")
            append("\nThank you for your order!")
        }
        receiptTextView.text = receiptText

        trackOrderButton.setOnClickListener {
            val intent = Intent(this, OrderTrackingActivity::class.java)
            intent.putExtra("orderId", orderId)
            startActivity(intent)
            finish()
        }
    }

    private fun saveAsFavoriteOrder(name: String, cartItems: List<Pair<Product, Int>>) {
        lifecycleScope.launch {
            try {
                Log.d("CheckoutActivity", "Starting to save favorite order with name: $name")
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userEmail = sharedPreferences.getString("loggedInUserEmail", null)
                
                if (userEmail == null) {
                    Log.e("CheckoutActivity", "No user email found in SharedPreferences")
                    Toast.makeText(this@CheckoutActivity, "Please log in to save favorite orders", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                val user = repository.getUserByEmail(userEmail)
                if (user == null) {
                    Log.e("CheckoutActivity", "No user found for email: $userEmail")
                    Toast.makeText(this@CheckoutActivity, "User not found", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (cartItems.isEmpty()) {
                    Log.e("CheckoutActivity", "Cart is empty, cannot save favorite order")
                    Toast.makeText(this@CheckoutActivity, "Your cart is empty", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // Create and save favorite order with items
                val favoriteOrder = FavoriteOrder(
                    userId = user.id,
                    name = name
                )
                
                Log.d("CheckoutActivity", "Inserting favorite order: $favoriteOrder")
                repository.insertFavoriteOrder(favoriteOrder, cartItems)
                Log.d("CheckoutActivity", "Favorite order saved successfully")
                Toast.makeText(this@CheckoutActivity, "Favorite order saved!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("CheckoutActivity", "Error saving favorite order", e)
                Toast.makeText(this@CheckoutActivity, "Error saving favorite order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isPaymentProcessed", isPaymentProcessed)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isPaymentProcessed = savedInstanceState.getBoolean("isPaymentProcessed", false)
    }
}