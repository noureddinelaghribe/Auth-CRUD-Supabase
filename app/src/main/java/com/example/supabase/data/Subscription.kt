package com.example.supabase.data

data class Subscription(
    val plan_id: Long? = null,
    val user_id: String? = null,
    val start_date: String? = null,
    val end_date: String? = null,
    val status: String? = null,
    val usage_words: Long? = null,
    val usage_files: Long? = null
)
