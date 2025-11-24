package com.example.supabase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.supabase.R
import com.example.supabase.data.History

class HistoryAdapter(private val historyList: List<History>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    var onItemClick: ((History) -> Unit)? = null

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvActionType: TextView = itemView.findViewById(R.id.tvActionType)
        val tvInputText: TextView = itemView.findViewById(R.id.tvInputText)
        val tvOutputText: TextView = itemView.findViewById(R.id.tvOutputText)
        val tvLanguage: TextView = itemView.findViewById(R.id.tvLanguage)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(historyList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]

        holder.tvActionType.text = history.action_type ?: "Unknown"
        holder.tvInputText.text = "Input: ${history.input_text ?: "N/A"}"
        holder.tvOutputText.text = "Output: ${history.output_text ?: "N/A"}"
        holder.tvLanguage.text = "Language: ${history.language ?: "N/A"}"
        holder.tvCreatedAt.text = history.created_at?.take(10) ?: "N/A"
    }

    override fun getItemCount(): Int = historyList.size
}