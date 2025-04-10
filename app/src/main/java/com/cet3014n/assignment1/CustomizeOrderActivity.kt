package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class CustomizeOrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customize_order)

        val item = intent.getSerializableExtra("menuItem") as? CoffeeMenuItem
            ?: return finish() // Close if no item is passed

        val itemImage = findViewById<ImageView>(R.id.item_image)
        val itemName = findViewById<TextView>(R.id.item_name)
        val itemPrice = findViewById<TextView>(R.id.item_price)
        val itemDescription = findViewById<TextView>(R.id.item_description)
        val customizationLabel = findViewById<TextView>(R.id.customization_label)
        val sugarCheckbox = findViewById<CheckBox>(R.id.sugar_checkbox)
        val sizeLabel = findViewById<TextView>(R.id.size_label)
        val sizeRadioGroup = findViewById<RadioGroup>(R.id.size_radio_group)
        val volumeLabel = findViewById<TextView>(R.id.volume_label)
        val quantityLabel = findViewById<TextView>(R.id.quantity_label)
        val quantityText = findViewById<TextView>(R.id.quantity_text)
        val decreaseButton = findViewById<Button>(R.id.decrease_quantity)
        val increaseButton = findViewById<Button>(R.id.increase_quantity)
        val addToCartButton = findViewById<Button>(R.id.add_to_cart_button)
        val backButton = findViewById<Button>(R.id.back_button)

        // Handle back button click
        backButton.setOnClickListener {
            finish()
        }

        // Populate item details
        itemImage.setImageResource(item.imageResId)
        itemName.text = item.name
        itemPrice.text = "RM${item.price}"
        itemDescription.text = item.description


        // Check category and show/hide customization options
        val isCustomizable = item.category == "Coffee" || item.category == "Tea"
        if (isCustomizable) {
            customizationLabel.visibility = View.VISIBLE
            sugarCheckbox.visibility = View.VISIBLE
            sizeLabel.visibility = View.VISIBLE
            sizeRadioGroup.visibility = View.VISIBLE
            volumeLabel.visibility = View.VISIBLE
        } else {
            customizationLabel.visibility = View.GONE
            sugarCheckbox.visibility = View.GONE
            sizeLabel.visibility = View.GONE
            sizeRadioGroup.visibility = View.GONE
            volumeLabel.visibility = View.GONE

            // Adjust constraints: Connect quantity_label directly to item_description
            val constraintLayout = findViewById<ConstraintLayout>(R.id.constraint_layout)
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.connect(
                R.id.quantity_label,
                ConstraintSet.TOP,
                R.id.item_description,
                ConstraintSet.BOTTOM,
                16
            )
            constraintSet.applyTo(constraintLayout)
        }

        // Handle size selection and volume/price updates (only for Coffee/Tea)
        var priceMultiplier = 1.0
        var volume = 160 // Default for small
        if (isCustomizable) {
            sizeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.size_small -> {
                        priceMultiplier = 1.0
                        volume = 160
                    }
                    R.id.size_medium -> {
                        priceMultiplier = 1.2
                        volume = 240
                    }
                    R.id.size_large -> {
                        priceMultiplier = 1.5
                        volume = 320
                    }
                }
                itemPrice.text = "RM${String.format("%.2f", item.price * priceMultiplier)}"
                volumeLabel.text = "Volume: $volume ml"
            }
        }

        // Quantity handling
        var quantity = 1
        quantityText.text = quantity.toString()

        decreaseButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityText.text = quantity.toString()
            }
        }

        increaseButton.setOnClickListener {
            quantity++
            quantityText.text = quantity.toString()
        }

        // Add to Cart
        addToCartButton.setOnClickListener {
            val customizedItem = if (isCustomizable) {
                item.copy(
                    price = item.price * priceMultiplier,
                    description = buildString {
                        append(item.description)
                        if (sugarCheckbox.isChecked) append(", with sugar")
                        append(", Size: ")
                        append(when (sizeRadioGroup.checkedRadioButtonId) {
                            R.id.size_small -> "Small"
                            R.id.size_medium -> "Medium"
                            R.id.size_large -> "Large"
                            else -> "Small"
                        })
                    }
                )
            } else {
                item // No customization for Pastries
            }
            CartManager.addItem(customizedItem, quantity)
            Toast.makeText(this, "$quantity ${item.name}(s) added to cart", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}