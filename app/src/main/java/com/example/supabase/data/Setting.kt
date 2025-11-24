package com.example.supabase.data

import com.google.gson.annotations.SerializedName

data class Setting(

    @SerializedName("id")
    val id: Long,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("verstion")
    val verstion: String,

    @SerializedName("update_mode")
    val updateMode: Boolean,

    @SerializedName("update_url")
    val updateUrl: String,

    @SerializedName("maintenance_mode")
    val maintenanceMode: Boolean,

    @SerializedName("maintenance_message")
    val maintenanceMessage: String,

    @SerializedName("apis_json")
    val apisJson: List<ApiItem>
)