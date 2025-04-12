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

    private var appliedDiscount: Double = 0.0
    private var appliedPromoCode: String? = null
    private var deliveryAddress: String? = null

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
                    deliveryAddress = null
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
                    appliedDiscount = discount
                    appliedPromoCode = promoCode
                    Toast.makeText(this, "Promo code applied!", Toast.LENGTH_SHORT).show()
                    updateTotalAmount()
                } else {
                    Toast.makeText(this, "Invalid promo code", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Setup proceed button
        proceedButton.setOnClickListener {
            deliveryAddress = deliveryAddressEditText.text.toString()
            val deliveryOption = if (deliveryRadioGroup.checkedRadioButtonId == R.id.delivery_radio) {
                if (deliveryAddress.isNullOrEmpty()) {
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
                putExtra("subtotal", calculateSubtotal())
                putExtra("total", calculateTotal())
                putExtra("promoCode", appliedPromoCode)
                putExtra("discount", appliedDiscount)
            }
            startActivity(intent)
        }

        // Update initial total amount
        updateTotalAmount()
    }

    private fun calculateSubtotal(): Double {
        return CartManager.getItems().sumOf { (item, quantity) -> item.price * quantity }
    }

    private fun calculateTotal(): Double {
        val subtotal = calculateSubtotal()
        return subtotal * (1 - appliedDiscount)
    }

     fun updateTotalAmount() {
        val subtotal = calculateSubtotal()
        val total = calculateTotal()
        totalAmountTextView.text = "Total: RM ${String.format("%.2f", total)}"
    }
}
