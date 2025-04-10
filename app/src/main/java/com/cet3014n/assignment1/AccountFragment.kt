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
import org.json.JSONArray
import org.json.JSONObject

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

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

        // Load user data from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInUserEmail = sharedPreferences.getString("loggedInUserEmail", null)

        if (loggedInUserEmail == null) {
            usernameText.text = "Not logged in"
            emailText.text = ""
            phoneText.text = ""
            addressText.text = ""
            editProfileButton.visibility = View.GONE
            logoutButton.visibility = View.GONE
            return view
        }

        // Retrieve the list of users
        val usersJsonString = sharedPreferences.getString("users", null)
        var username = "User"
        var email = "Not set"
        var phone = "Not set"
        var address = "Not set"

        if (usersJsonString != null) {
            val usersArray = JSONArray(usersJsonString)
            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                if (user.getString("email") == loggedInUserEmail) {
                    username = user.optString("username", "User")
                    email = user.optString("email", "Not set")
                    phone = user.optString("phone", "Not set")
                    address = user.optString("address", "Not set")
                    break
                }
            }
        }

        // Display user data
        usernameText.text = "Username: $username"
        emailText.text = "Email: $email"
        phoneText.text = "Phone: $phone"
        addressText.text = "Address: $address"

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
                editUsername.setText(username)
                editEmail.setText(email)
                editPhone.setText(phone)
                editAddress.setText(address)

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

            // Retrieve the list of users again to update
            val usersJsonStringUpdated = sharedPreferences.getString("users", null)
            if (usersJsonStringUpdated == null) {
                Toast.makeText(requireContext(), "Error: User data not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usersArray = JSONArray(usersJsonStringUpdated)
            var userFound = false
            var oldEmail = ""

            // Find the user and update their data
            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                if (user.getString("email") == loggedInUserEmail) {
                    oldEmail = user.getString("email")
                    user.put("username", newUsername)
                    user.put("email", newEmail)
                    user.put("phone", newPhone)
                    user.put("address", newAddress)
                    userFound = true
                    break
                }
            }

            if (!userFound) {
                Toast.makeText(requireContext(), "Error: User not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check for email conflicts with other users
            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                val userEmail = user.getString("email")
                if (userEmail == newEmail && userEmail != oldEmail) {
                    Toast.makeText(requireContext(), "Email already in use by another user", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Save the updated users array back to SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString("users", usersArray.toString())
            // Update the logged-in user email if it changed
            if (newEmail != loggedInUserEmail) {
                editor.putString("loggedInUserEmail", newEmail)
            }
            editor.apply()

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
        }

        // Logout button click
        logoutButton.setOnClickListener {
            // Navigate to LogoutActivity
            val intent = Intent(requireActivity(), LogoutActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}