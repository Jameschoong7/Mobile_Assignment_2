package com.cet3014n.assignment1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        supportActionBar?.apply {
            title = "Checkout"
        }

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
        subtotal = intent.getDoubleExtra("subtotal", 0.0)
        total = intent.getDoubleExtra("total", 0.0)
        promoCode = intent.getStringExtra("promoCode")
        discount = intent.getDoubleExtra("discount", 0.0)

        // Display the total with discount (if applied)
        totalAmountTextView.text = buildString {
            append("Subtotal: RM%.2f".format(subtotal))
            if (discount > 0 && promoCode != null) {
                append("\nDiscount ($promoCode): -RM%.2f".format(subtotal * discount))
            }
            append("\nTotal: RM%.2f".format(total))
        }

        // Initially hide the confirmation layout
        paymentConfirmationLayout.visibility = View.GONE

        // Load saved payment details if they exist
        val sharedPreferences = getSharedPreferences("PaymentPrefs", MODE_PRIVATE)
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

        // Generate a receipt
        val cartItems = CartManager.getItems()
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

        // Show confirmation and receipt
        paymentConfirmationLayout.visibility = View.VISIBLE
        receiptTextView.text = receipt

        // Hide payment form and related UI elements
        paymentFormLayout.visibility = View.GONE
        paymentMethodGroup.visibility = View.GONE
        findViewById<TextView>(R.id.payment_method_label).visibility = View.GONE
        payButton.visibility = View.GONE

        // Create an order for tracking
        val orderId = "ORDER_${System.currentTimeMillis()}"
        OrderManager.createOrder(orderId, cartItems, deliveryOption)

        // Clear the cart after successful payment
        CartManager.clearCart()
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