package com.cet3014n.assignment1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {
    private lateinit var usernameText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView
    private lateinit var addressText: TextView
    private lateinit var editUsername: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var editAddress: EditText
    private lateinit var editProfileButton: Button
    private lateinit var saveProfileButton: Button
    private lateinit var logoutButton: Button
    private var isEditing = false
    private lateinit var repository: CoffeeShopRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Initialize repository
        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(requireContext()).coffeeShopDao())

        // Initialize views
        usernameText = view.findViewById(R.id.username_text)
        emailText = view.findViewById(R.id.email_text)
        phoneText = view.findViewById(R.id.phone_text)
        addressText = view.findViewById(R.id.address_text)
        editUsername = view.findViewById(R.id.edit_username)
        editEmail = view.findViewById(R.id.edit_email)
        editPhone = view.findViewById(R.id.edit_phone)
        editAddress = view.findViewById(R.id.edit_address)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        saveProfileButton = view.findViewById(R.id.save_profile_button)
        logoutButton = view.findViewById(R.id.logout_button)

        // Load user data from database
        loadUserData()

        // Set initial visibility
        editUsername.visibility = View.GONE
        editEmail.visibility = View.GONE
        editPhone.visibility = View.GONE
        editAddress.visibility = View.GONE
        saveProfileButton.visibility = View.GONE

        // Edit Profile button click
        editProfileButton.setOnClickListener {
            if (!isEditing) {
                // Switch to edit mode
                isEditing = true
                editUsername.setText(usernameText.text.toString().replace("Username: ", ""))
                editEmail.setText(emailText.text.toString().replace("Email: ", ""))
                editPhone.setText(phoneText.text.toString().replace("Phone: ", ""))
                editAddress.setText(addressText.text.toString().replace("Address: ", ""))

                usernameText.visibility = View.GONE
                emailText.visibility = View.GONE
                phoneText.visibility = View.GONE
                addressText.visibility = View.GONE
                editUsername.visibility = View.VISIBLE
                editEmail.visibility = View.VISIBLE
                editPhone.visibility = View.VISIBLE
                editAddress.visibility = View.VISIBLE
                editProfileButton.visibility = View.GONE
                saveProfileButton.visibility = View.VISIBLE
            }
        }

        // Save Profile button click
        saveProfileButton.setOnClickListener {
            // Save updated data
            val newUsername = editUsername.text.toString().trim()
            val newEmail = editEmail.text.toString().trim()
            val newPhone = editPhone.text.toString().trim()
            val newAddress = editAddress.text.toString().trim()

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Username and email cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Get current user's email from SharedPreferences
                    val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val currentEmail = sharedPreferences.getString("loggedInUserEmail", null)
                    
                    if (currentEmail == null) {
                        Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // Get current user
                    val currentUser = repository.getUserByEmail(currentEmail)
                    if (currentUser == null) {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // Check if new email is already taken by another user
                    if (newEmail != currentEmail) {
                        val existingUser = repository.getUserByEmail(newEmail)
                        if (existingUser != null) {
                            Toast.makeText(requireContext(), "Email already in use by another user", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                    }

                    // Update user
                    val updatedUser = currentUser.copy(
                        username = newUsername,
                        email = newEmail,
                        phone = newPhone,
                        address = newAddress
                    )
                    repository.updateUser(updatedUser)

                    // Update SharedPreferences if email changed
                    if (newEmail != currentEmail) {
                        sharedPreferences.edit().putString("loggedInUserEmail", newEmail).apply()
                    }

                    // Update display
                    usernameText.text = "Username: $newUsername"
                    emailText.text = "Email: $newEmail"
                    phoneText.text = "Phone: $newPhone"
                    addressText.text = "Address: $newAddress"

                    // Switch back to view mode
                    isEditing = false
                    usernameText.visibility = View.VISIBLE
                    emailText.visibility = View.VISIBLE
                    phoneText.visibility = View.VISIBLE
                    addressText.visibility = View.VISIBLE
                    editUsername.visibility = View.GONE
                    editEmail.visibility = View.GONE
                    editPhone.visibility = View.GONE
                    editAddress.visibility = View.GONE
                    editProfileButton.visibility = View.VISIBLE
                    saveProfileButton.visibility = View.GONE

                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Logout button click
        logoutButton.setOnClickListener {
            val intent = Intent(requireActivity(), LogoutActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                // Get current user's email from SharedPreferences
                val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userEmail = sharedPreferences.getString("loggedInUserEmail", null)

                if (userEmail == null) {
                    usernameText.text = "Not logged in"
                    emailText.text = ""
                    phoneText.text = ""
                    addressText.text = ""
                    editProfileButton.visibility = View.GONE
                    logoutButton.visibility = View.GONE
                    return@launch
                }

                // Get user from database
                val user = repository.getUserByEmail(userEmail)
                if (user == null) {
                    usernameText.text = "User not found"
                    emailText.text = ""
                    phoneText.text = ""
                    addressText.text = ""
                    editProfileButton.visibility = View.GONE
                    logoutButton.visibility = View.GONE
                    return@launch
                }

                // Display user data
                usernameText.text = "Username: ${user.username}"
                emailText.text = "Email: ${user.email}"
                phoneText.text = "Phone: ${user.phone}"
                addressText.text = "Address: ${user.address}"
                editProfileButton.visibility = View.VISIBLE
                logoutButton.visibility = View.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}