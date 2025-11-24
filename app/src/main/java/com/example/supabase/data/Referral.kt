package com.example.supabase.data

import java.util.*

data class Referral(
    val id: Long? = null,
    val created_at: String? = null,
    val referrer_id: String? = null,
    val referred_id: String? = null,
    val reward_given: Boolean? = null
)
