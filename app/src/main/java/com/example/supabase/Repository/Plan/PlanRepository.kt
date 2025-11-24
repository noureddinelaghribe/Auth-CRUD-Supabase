package com.example.supabase.Repository.Plan

import android.util.Log
import com.example.supabase.data.Plan

class PlanRepository {

    companion object {
        private const val TAG = "PlanRepository"
    }

    init {
        PlanRetrofitInstance.initialize()
    }

    /** دالة تجلب جميع الباقات المتاحة من الخادم */
    suspend fun getPlans(): Result<List<Plan>> = try {
        val response = PlanRetrofitInstance.api.getPlans()
        if (response.isEmpty()) {
            throw IllegalStateException("لا توجد باقات متاحة")
        }
        Log.d(TAG, "Fetched plans: $response")
        Result.success(response)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching plans", e)
        Result.failure(e)
    }
}