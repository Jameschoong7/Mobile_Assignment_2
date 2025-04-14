package com.cet3014n.assignment1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class RewardTransactionAdapter : RecyclerView.Adapter<RewardTransactionAdapter.TransactionViewHolder>() {
    private var transactions: List<RewardTransaction> = emptyList()

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeText: TextView = itemView.findViewById(R.id.transaction_type)
        val pointsText: TextView = itemView.findViewById(R.id.transaction_points)
        val descriptionText: TextView = itemView.findViewById(R.id.transaction_description)
        val dateText: TextView = itemView.findViewById(R.id.transaction_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reward_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        
        holder.typeText.text = when (transaction.type) {
            TransactionType.EARNED -> "Earned"
            TransactionType.REDEEMED -> "Redeemed"
        }
        
        holder.pointsText.text = "${transaction.points} points"
        holder.descriptionText.text = transaction.description
        
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        holder.dateText.text = dateFormat.format(Date(transaction.timestamp))
    }

    override fun getItemCount(): Int = transactions.size

    fun updateTransactions(newTransactions: List<RewardTransaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
} 