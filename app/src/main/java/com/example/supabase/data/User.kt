package com.example.supabase.data

data class User(
    val id: Long? = null,
    val user_id: String? = null,
    val name: String,
    val email: String,
    val device_id: String? = null,
    val role: String? = "USER"
)