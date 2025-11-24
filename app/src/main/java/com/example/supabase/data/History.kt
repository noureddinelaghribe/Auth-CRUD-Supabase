package com.example.supabase.data

data class History (
    val id: Long? = null,
    val user_id: String? = null,
    val created_at: String? = null,
    val input_text: String? = null,
    val output_text: String? = null,
    val html_text: String? = null,
    val action_type: String? = null,
    val language: String? = null,
    val platform: String = "Android",
)