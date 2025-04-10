package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.email)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerButton = findViewById<Button>(R.id.register_button)
        val forgotPasswordText = findViewById<TextView>(R.id.forgot_password)

        val sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Retrieve the list of users from SharedPreferences
            val usersJsonString = sharedPrefs.getString("users", null)
            if (usersJsonString == null) {
                Toast.makeText(this, "No users registered", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Parse the users array
            val usersArray = JSONArray(usersJsonString)
            var userFound = false

            // Check if the email and password match any user
            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                val storedEmail = user.getString("email")
                val storedPassword = user.getString("password")

                if (storedEmail == email && storedPassword == password) {
                    userFound = true
                    // Save the logged-in user's email to SharedPreferences
                    val editor = sharedPrefs.edit()
                    editor.putString("loggedInUserEmail", email)
                    editor.apply()

                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    break
                }
            }

            if (!userFound) {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Redirect to ResetPasswordActivity
        forgotPasswordText.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }
}