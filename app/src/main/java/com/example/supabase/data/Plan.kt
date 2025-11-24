package com.example.supabase.data

import com.google.gson.annotations.SerializedName

data class Plan(

    @SerializedName("plan_id")
    val planId: Long = 0,

    val name: String = "",
    val description: String = "",
    val price: String = "0",

    @SerializedName("word_limit")
    val wordLimit: String = "0",

    @SerializedName("file_limit")
    val fileLimit: String = "0",

    @SerializedName("input_limit")
    val inputLimit: String = "0"

)