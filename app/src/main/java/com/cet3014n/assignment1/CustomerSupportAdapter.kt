package com.cet3014n.assignment1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CustomerSupportAdapter : RecyclerView.Adapter<CustomerSupportAdapter.ViewHolder>() {
    private var feedbackList: List<Pair<SupportFeedback, User>> = emptyList()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameText: TextView = view.findViewById(R.id.username_text)
        val issueTypeText: TextView = view.findViewById(R.id.issue_type_text)
        val descriptionText: TextView = view.findViewById(R.id.description_text)
        val timestampText: TextView = view.findViewById(R.id.timestamp_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer_support, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (feedback, user) = feedbackList[position]
        
        holder.usernameText.text = "User: ${user.username}"
        holder.issueTypeText.text = "Issue Type: ${feedback.issueType}"
        holder.descriptionText.text = feedback.description
        holder.timestampText.text = dateFormat.format(Date(feedback.timestamp))
    }

    override fun getItemCount() = feedbackList.size

    fun updateFeedback(newFeedbackList: List<Pair<SupportFeedback, User>>) {
        feedbackList = newFeedbackList
        notifyDataSetChanged()
    }
} 