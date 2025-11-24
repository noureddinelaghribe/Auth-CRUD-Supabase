package com.example.supabase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.supabase.R
import com.example.supabase.data.Referral
class ReferralAdapter :
    RecyclerView.Adapter<ReferralAdapter.ReferralViewHolder>() {

    private val referralList = mutableListOf<Referral>()

    fun submitList(items: List<Referral>) {
        referralList.clear()
        referralList.addAll(items)
        notifyDataSetChanged()
    }

    var onItemClick: ((Referral) -> Unit)? = null

    inner class ReferralViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvReferrerId: TextView = itemView.findViewById(R.id.tvReferrerId)
        val tvReferredId: TextView = itemView.findViewById(R.id.tvReferredId)
        val tvRewardGiven: TextView = itemView.findViewById(R.id.tvRewardGiven)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(referralList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReferralViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_referral, parent, false)
        return ReferralViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReferralViewHolder, position: Int) {
        val referral = referralList[position]

        holder.tvReferrerId.text = "Referrer: ${referral.referrer_id?.toString()?.take(8) ?: "N/A"}"
        holder.tvReferredId.text = "Referred: ${referral.referred_id?.toString()?.take(8) ?: "N/A"}"
        holder.tvRewardGiven.text = "Reward: ${if (referral.reward_given == true) "Given" else "Pending"}"
        holder.tvCreatedAt.text = referral.created_at?.take(10) ?: "N/A"
    }

    override fun getItemCount(): Int = referralList.size
}

