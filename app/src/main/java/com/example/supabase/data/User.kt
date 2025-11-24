package com.example.supabase.data

data class User(
    val id: Long? = null,
    val user_id: String? = null,
    val password_hash: String,
    val name: String,
    val email: String,
    val role: String? = "USER"
)