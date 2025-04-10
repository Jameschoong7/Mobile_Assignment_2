package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {
    private lateinit var totalAmountTextView: TextView
    private lateinit var promoCodeEditText: EditText
    private lateinit var applyPromoButton: Button
    private lateinit var proceedButton: Button
    private lateinit var cartAdapter: CartAdapter
    private lateinit var backButton: Button

    // Map of promo codes and their discount percentages (e.g., "SAVE10" -> 10% off)
    private val promoCodes = mapOf(
        "SAVE10" to 0.10, // 10% off
        "SAVE20" to 0.20, // 20% off
        "SAVE5" to 0.05 // 5% off
    )

    // Track the applied discount and promo code
    private var appliedDiscount: Double = 0.0 // Discount percentage (e.g., 0.10 for 10%)
    private var appliedPromoCode: String? = null // Store the applied promo code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_cart)
        totalAmountTextView = findViewById(R.id.total_amount)
        promoCodeEditText = findViewById(R.id.promo_code)
        applyPromoButton = findViewById(R.id.apply_promo)
        proceedButton = findViewById(R.id.proceed_button)
        backButton = findViewById(R.id.back_button)

        // Handle back button click
        backButton.setOnClickListener {
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter()
        recyclerView.adapter = cartAdapter

        val deliveryRadioGroup = findViewById<RadioGroup>(R.id.delivery_radio_group)

        applyPromoButton.setOnClickListener {
            val promoCode = promoCodeEditText.text.toString().trim().uppercase() // Case-insensitive
            if (promoCode.isEmpty()) {
                Toast.makeText(this, "Please enter a promo code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the promo code is valid
            val discount = promoCodes[promoCode]
            if (discount != null) {
                appliedDiscount = discount
                appliedPromoCode = promoCode
                Toast.makeText(this, "Promo code $promoCode applied! ${(discount * 100).toInt()}% off", Toast.LENGTH_SHORT).show()
            } else {
                appliedDiscount = 0.0
                appliedPromoCode = null
                Toast.makeText(this, "Invalid promo code", Toast.LENGTH_SHORT).show()
            }

            // Update the total with the applied discount
            updateTotalAmount(CartManager.getItems())
        }

        proceedButton.setOnClickListener {
            val deliveryOption = when (deliveryRadioGroup.checkedRadioButtonId) {
                R.id.radio_pickup -> "Pickup"
                R.id.radio_delivery -> "Delivery"
                else -> {
                    Toast.makeText(this, "Please select a delivery option", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("deliveryOption", deliveryOption)
            // Pass the total amount and applied promo code to CheckoutActivity
            val cartItems = CartManager.getItems()
            val subtotal = cartItems.sumOf { it.first.price * it.second }
            val total = subtotal * (1 - appliedDiscount)
            intent.putExtra("subtotal", subtotal)
            intent.putExtra("total", total)
            intent.putExtra("promoCode", appliedPromoCode)
            intent.putExtra("discount", appliedDiscount)
            startActivity(intent)
        }

        updateTotalAmount(CartManager.getItems())
    }

    fun updateTotalAmount(cartItems: List<Pair<CoffeeMenuItem, Int>>) {
        val subtotal = cartItems.sumOf { it.first.price * it.second }
        val discountAmount = subtotal * appliedDiscount
        val total = subtotal - discountAmount
        totalAmountTextView.text = buildString {
            append("Subtotal: RM%.2f".format(subtotal))
            if (appliedDiscount > 0) {
                append("\nDiscount: -RM%.2f".format(discountAmount))
                append("\nTotal: RM%.2f".format(total))
            } else {
                append("\nTotal: RM%.2f".format(total))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cartAdapter.refreshItems()
        updateTotalAmount(CartManager.getItems())
    }
}