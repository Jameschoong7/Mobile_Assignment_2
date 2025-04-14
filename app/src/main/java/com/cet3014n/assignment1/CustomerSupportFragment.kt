package com.cet3014n.assignment1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CustomerSupportFragment : Fragment() {
    private lateinit var repository: CoffeeShopRepository
    private lateinit var issueTypeGroup: RadioGroup
    private lateinit var descriptionInput: EditText
    private lateinit var submitButton: Button
    private lateinit var callButton: Button
    private lateinit var videoCallButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customer_support, container, false)
        
        // Initialize repository
        repository = CoffeeShopRepository(CoffeeShopDatabase.getDatabase(requireContext()).coffeeShopDao())
        
        // Initialize views
        issueTypeGroup = view.findViewById(R.id.issue_type_group)
        descriptionInput = view.findViewById(R.id.description_input)
        submitButton = view.findViewById(R.id.submit_button)
        callButton = view.findViewById(R.id.call_button)
        videoCallButton = view.findViewById(R.id.video_call_button)
        
        // Setup button click listeners
        callButton.setOnClickListener {
            makePhoneCall()
        }
        
        videoCallButton.setOnClickListener {
            startVideoCall()
        }
        
        submitButton.setOnClickListener {
            submitFeedback()
        }
        
        return view
    }

    private fun makePhoneCall() {
        val phoneNumber = "tel:1234567890"
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber))
        startActivity(intent)
    }

    private fun startVideoCall() {
        // For video call, we'll use a simple video chat app intent

        val videoCallUri = Uri.parse("https://meet.google.com/new") // Example using Google Meet
        val intent = Intent(Intent.ACTION_VIEW, videoCallUri)
        startActivity(intent)
    }

    private fun submitFeedback() {
        val issueType = when (issueTypeGroup.checkedRadioButtonId) {
            R.id.radio_order_issue -> "Order Issue"
            R.id.radio_payment_issue -> "Payment Issue"
            R.id.radio_other -> "Other"
            else -> "Unknown"
        }
        
        val description = descriptionInput.text.toString().trim()
        
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please describe your issue", Toast.LENGTH_SHORT).show()
            return
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
                val userEmail = sharedPreferences.getString("loggedInUserEmail", null)
                
                if (userEmail == null) {
                    Toast.makeText(requireContext(), "Please log in to submit feedback", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                val user = repository.getUserByEmail(userEmail)
                if (user == null) {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                // Create and save feedback
                val feedback = SupportFeedback(
                    userId = user.id,
                    issueType = issueType,
                    description = description,
                    timestamp = System.currentTimeMillis()
                )
                
                repository.insertSupportFeedback(feedback)
                
                // Clear form
                descriptionInput.text.clear()
                issueTypeGroup.clearCheck()
                
                Toast.makeText(requireContext(), "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error submitting feedback: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 