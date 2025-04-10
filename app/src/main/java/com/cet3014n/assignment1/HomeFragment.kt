package com.cet3014n.assignment1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cet3014n.assignment1.R
import org.json.JSONArray
import org.json.JSONObject

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Get logged-in user's email from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val loggedInUserEmail = sharedPreferences.getString("loggedInUserEmail", null)

        // Initialize the username variable with a default value
        var username = "User"

        if (loggedInUserEmail != null) {
            // Retrieve the list of users from SharedPreferences
            val usersJsonString = sharedPreferences.getString("users", null)
            if (usersJsonString != null) {
                val usersArray = JSONArray(usersJsonString)
                // Loop through the users to find the matching user by email
                for (i in 0 until usersArray.length()) {
                    val user = usersArray.getJSONObject(i)
                    if (user.getString("email") == loggedInUserEmail) {
                        // Set the username of the logged-in user
                        username = user.optString("username", "User")
                        break
                    }
                }
            }
        }

        // Set greeting
        val greetingTextView: TextView = view.findViewById(R.id.greeting_text)
        greetingTextView.text = "Hello, $username !"

        // Set promotion image (hardcoded)
        val promotionImageView: ImageView = view.findViewById(R.id.promotion_image)
        promotionImageView.setImageResource(R.drawable.promotion_banner) // Replace with your image

        return view
    }
}
