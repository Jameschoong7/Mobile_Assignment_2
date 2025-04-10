package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val emailInput = findViewById<EditText>(R.id.reset_email)
        val newPasswordInput = findViewById<EditText>(R.id.new_password)
        val resetButton = findViewById<Button>(R.id.reset_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)

        val sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        resetButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val newPassword = newPasswordInput.text.toString().trim()

            // Validation
            if (email.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please enter email and new password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPassword.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val storedUser = sharedPrefs.getString("user", null)
            if (storedUser != null) {
                val json = JSONObject(storedUser)
                if (json.getString("email") == email) {
                    // Update password
                    json.put("password", newPassword)
                    with(sharedPrefs.edit()) {
                        putString("user", json.toString())
                        apply()
                    }
                    Toast.makeText(this, "Password reset successful. Please log in.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No user registered", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}