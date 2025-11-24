package com.example.supabase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.supabase.R
import com.example.supabase.data.Subscription
import android.graphics.Color
import com.google.android.material.card.MaterialCardView
import android.util.Log

class SubscriptionAdapter(private var items: List<Subscription>) :
    RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    inner class SubscriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView as MaterialCardView
        val tvPlanId: TextView = itemView.findViewById(R.id.tvPlanId)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvDates: TextView = itemView.findViewById(R.id.tvDates)
        val tvUsage: TextView = itemView.findViewById(R.id.tvUsage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subscription, parent, false)
        return SubscriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val sub = items[position]
        holder.tvPlanId.text = "Plan: ${sub.plan_id ?: "-"}" 
        val status = (sub.status ?: "-")
        holder.tvStatus.text = "Status: $status"
        val start = sub.start_date?.take(10) ?: "-"
        val end = sub.end_date?.take(10) ?: "-"
        holder.tvDates.text = "Dates: $start → $end"
        val words = sub.usage_words ?: 0
        val files = sub.usage_files ?: 0
        holder.tvUsage.text = "Usage: ${words}w, ${files}f"

        // Color by status
        val normalizedStatus = status.trim().lowercase(java.util.Locale.ROOT)
        Log.d("SubscriptionAdapter", "Status: '$status' -> normalized: '$normalizedStatus'")
        
        when (normalizedStatus) {
            "active" -> {
                holder.card.setCardBackgroundColor(Color.parseColor("#E8F5E9")) // green 50
                holder.tvStatus.setTextColor(Color.parseColor("#2E7D32")) // green 800
                Log.d("SubscriptionAdapter", "Applied GREEN for active")
            }
            "expired" -> {
                holder.card.setCardBackgroundColor(Color.parseColor("#FAFAFA")) // grey 50
                holder.tvStatus.setTextColor(Color.parseColor("#6D6D6D")) // grey 700
                Log.d("SubscriptionAdapter", "Applied GREY for expired")
            }
            "cancelled", "canceled" -> {
                holder.card.setCardBackgroundColor(Color.parseColor("#FFEBEE")) // red 50
                holder.tvStatus.setTextColor(Color.parseColor("#C62828")) // red 800
                Log.d("SubscriptionAdapter", "Applied RED for cancelled")
            }
            else -> {
                holder.card.setCardBackgroundColor(Color.parseColor("#E3F2FD")) // light blue instead of white
                holder.tvStatus.setTextColor(Color.parseColor("#424242"))
                Log.d("SubscriptionAdapter", "Applied DEFAULT (light blue) for: $normalizedStatus")
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(list: List<Subscription>) {
        items = list
        notifyDataSetChanged()
    }
}
