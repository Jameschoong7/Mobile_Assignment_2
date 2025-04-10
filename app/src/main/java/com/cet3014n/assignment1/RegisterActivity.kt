package com.cet3014n.assignment1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

            // Access SharedPreferences
            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            // Retrieve the existing users (if any) or create a new JSONArray
            val usersJsonString = sharedPreferences.getString("users", null)
            val usersArray = if (usersJsonString != null) {
                JSONArray(usersJsonString)
            } else {
                JSONArray()
            }

            // Check if the email is already registered
            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                if (user.getString("email") == email) {
                    Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Create a new user JSON object
            val userJson = JSONObject().apply {
                put("username", username)
                put("email", email)
                put("password", password)
                put("phone", phone)
                put("address", address)
            }

            // Add the new user to the array
            usersArray.put(userJson)

            // Save the updated users array back to SharedPreferences
            editor.putString("users", usersArray.toString())
            editor.apply()

            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}