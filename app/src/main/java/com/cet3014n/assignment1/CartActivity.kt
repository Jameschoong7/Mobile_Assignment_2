package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
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
    private lateinit var deliveryRadioGroup: RadioGroup
    private lateinit var addressInputLayout: LinearLayout
    private lateinit var deliveryAddressEditText: EditText

    private val promoCodes = mapOf(
        "SAVE10" to 0.10,
        "SAVE20" to 0.20,
        "SAVE5" to 0.05
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Initialize views
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_cart)
        totalAmountTextView = findViewById(R.id.total_amount)
        promoCodeEditText = findViewById(R.id.promo_code)
        applyPromoButton = findViewById(R.id.apply_promo)
        proceedButton = findViewById(R.id.proceed_button)
        backButton = findViewById(R.id.back_button)
        deliveryRadioGroup = findViewById(R.id.delivery_radio_group)
        addressInputLayout = findViewById(R.id.address_input_layout)
        deliveryAddressEditText = findViewById(R.id.delivery_address)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter()
        recyclerView.adapter = cartAdapter

        // Setup back button
        backButton.setOnClickListener {
            finish()
        }

        // Setup delivery radio group listener
        deliveryRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.pickup_radio -> {
                    addressInputLayout.visibility = View.GONE
                }
                R.id.delivery_radio -> {
                    addressInputLayout.visibility = View.VISIBLE
                }
            }
        }

        // Setup promo code button
        applyPromoButton.setOnClickListener {
            val promoCode = promoCodeEditText.text.toString().trim()
            if (promoCode.isNotEmpty()) {
                val discount = promoCodes[promoCode]
                if (discount != null) {
                    CartManager.applyDiscount(discount, promoCode)
                    Toast.makeText(this, "Promo code applied!", Toast.LENGTH_SHORT).show()
                    updateTotalAmount()
                } else {
                    Toast.makeText(this, "Invalid promo code", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Setup proceed button
        proceedButton.setOnClickListener {
            val deliveryAddress = deliveryAddressEditText.text.toString()
            val deliveryOption = if (deliveryRadioGroup.checkedRadioButtonId == R.id.delivery_radio) {
                if (deliveryAddress.isEmpty()) {
                    Toast.makeText(this, "Please enter delivery address", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                "Delivery"
            } else {
                "Pickup"
            }

            val intent = Intent(this, CheckoutActivity::class.java).apply {
                putExtra("deliveryOption", deliveryOption)
                putExtra("deliveryAddress", deliveryAddress)
                putExtra("subtotal", CartManager.calculateSubtotal())
                putExtra("total", CartManager.calculateTotal())
                putExtra("promoCode", CartManager.getAppliedPromoCode())
                putExtra("discount", CartManager.getAppliedDiscount())
            }
            startActivity(intent)
        }

        // Update initial total amount
        updateTotalAmount()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the cart items and update UI
        cartAdapter.updateItems(CartManager.getItems())
        updateTotalAmount()
    }

    fun updateTotalAmount() {
        val subtotal = CartManager.calculateSubtotal()
        val total = CartManager.calculateTotal()
        val discount = CartManager.getAppliedDiscount()
        val promoCode = CartManager.getAppliedPromoCode()

        val text = buildString {
            append("Subtotal: RM${String.format("%.2f", subtotal)}")
            if (discount > 0 && promoCode != null) {
                append("\nDiscount ($promoCode): -RM${String.format("%.2f", subtotal * discount)}")
            }
            append("\nTotal: RM${String.format("%.2f", total)}")
        }
        totalAmountTextView.text = text
    }
}
