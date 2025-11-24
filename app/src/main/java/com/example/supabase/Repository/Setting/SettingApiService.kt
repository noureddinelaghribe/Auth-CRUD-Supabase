package com.example.supabase.Repository.Setting

import com.example.supabase.data.Setting
import retrofit2.http.GET

interface SettingApiService {

    //@GET("rest/v1/settings")
    @GET("rest/v1/settings?select=*&order=created_at.desc&limit=1")
    suspend fun getSettings(): List<Setting>

}