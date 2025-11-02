package com.example.supabase.data

data class ErrorResponse(
    val error: String? = null,
    val error_description: String? = null,
    val message: String? = null
)