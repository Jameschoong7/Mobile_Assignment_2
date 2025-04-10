package com.cet3014n.assignment1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.room.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var repository: CoffeeShopRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize the repository with the Room database instance
        val db = CoffeeShopDatabase.getDatabase(applicationContext)
        repository = CoffeeShopRepository(db.coffeeShopDao())

        val usernameEditText = findViewById<EditText>(R.id.register_username)
        val emailEditText = findViewById<EditText>(R.id.register_email)
        val passwordEditText = findViewById<EditText>(R.id.register_password)
        val phoneEditText = findViewById<EditText>(R.id.register_phone)
        val addressEditText = findViewById<EditText>(R.id.register_address)
        val registerButton = findViewById<Button>(R.id.register_button)
        val backButton = findViewById<Button>(R.id.back_button)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            GlobalScope.launch {
                // Check if email already exists
                val existingUser = repository.getUserByEmail(email)
                if (existingUser != null) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Email already registered", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Create new user object and insert into Room database
                val newUser = User(username = username, email = email, password = password, phone = phone, address = address)
                repository.insertUser(newUser)

                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
