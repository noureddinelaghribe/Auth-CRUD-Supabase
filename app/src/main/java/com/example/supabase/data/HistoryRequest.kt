package com.example.supabase.data

class HistoryRequest (
    val user_id: String? = null,
    val input_text: String? = null,
    val output_text: String? = null,
    val action_type: String? = null,
    val language: String? = null,
    val platform: String = "Android",
)