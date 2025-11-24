package com.example.supabase.data

data class ReferralCode(
    val id: Long? = null,
    val active: Boolean = true,
    val user_id: String? = null,
    val referral_code: String? = null
)