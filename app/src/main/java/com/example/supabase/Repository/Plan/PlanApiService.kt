package com.example.supabase.Repository.Plan

import com.example.supabase.data.Plan
import retrofit2.http.GET


interface PlanApiService {

    @GET("rest/v1/plans")
    suspend fun getPlans(): List<Plan>

}