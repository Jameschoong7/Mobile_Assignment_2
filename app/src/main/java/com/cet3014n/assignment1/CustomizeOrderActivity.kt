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

        val item = intent.getSerializableExtra("menuItem") as? Product ?: return finish()

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

        backButton.setOnClickListener {
            finish()
        }

        itemImage.setImageResource(item.imageResId)
        itemName.text = item.name
        itemPrice.text = "RM${String.format("%.2f", item.price)}"
        itemDescription.text = item.description

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
            val constraintLayout = findViewById<ConstraintLayout>(R.id.constraint_layout)
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.connect(addToCartButton.id, ConstraintSet.BOTTOM, constraintLayout.id, ConstraintSet.BOTTOM, 16)
            constraintSet.applyTo(constraintLayout)
        }

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

        // Setup size radio group listener
        sizeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val basePrice = item.price
            when (checkedId) {
                R.id.size_small -> {
                    itemPrice.text = "RM${String.format("%.2f", basePrice)}"
                    volumeLabel.text = "Volume: 160 ml"
                }
                R.id.size_medium -> {
                    itemPrice.text = "RM${String.format("%.2f", basePrice * 1.2)}"
                    volumeLabel.text = "Volume: 240 ml"
                }
                R.id.size_large -> {
                    itemPrice.text = "RM${String.format("%.2f", basePrice * 1.4)}"
                    volumeLabel.text = "Volume: 320 ml"
                }
            }
        }

        // Set initial price and volume
        itemPrice.text = "RM${String.format("%.2f", item.price)}"
        volumeLabel.text = "Volume: 160 ml"

        addToCartButton.setOnClickListener {
            val customizations = mutableListOf<String>()
            if (sugarCheckbox.isChecked) customizations.add("No Sugar")
            
            // Get the selected size and update price accordingly
            val sizeMultiplier = when (sizeRadioGroup.checkedRadioButtonId) {
                R.id.size_medium -> 1.2
                R.id.size_large -> 1.4
                else -> 1.0
            }
            
            val sizeText = when (sizeRadioGroup.checkedRadioButtonId) {
                R.id.size_medium -> "Medium Size"
                R.id.size_large -> "Large Size"
                else -> "Small Size"
            }
            customizations.add(sizeText)

            val customizedItem = item.copy(price = item.price * sizeMultiplier)
            CartManager.addItem(customizedItem, quantity)

            Toast.makeText(this, "${item.name} added to cart", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
