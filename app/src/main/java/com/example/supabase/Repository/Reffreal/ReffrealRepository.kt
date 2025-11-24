package com.example.supabase.Repository.Reffreal

import android.content.Context
import android.util.Log
import com.example.supabase.data.Referral
import com.example.supabase.utels.PreferencesHelper

class ReffrealRepository(context: Context) {

    companion object {
        private const val TAG = "ReferralRepository"
    }

    private val prefsHelper = PreferencesHelper(context)

    init {
        ReffrealRetrofitInstance.initialize(context)
    }

    /** دالة تجلب قائمة الإحالات الخاصة بالمستخدم الحالي */
    suspend fun getReferrals(): Result<List<Referral>> = try {
        val userId = prefsHelper.getUserId().orEmpty()
        val response = ReffrealRetrofitInstance.api.getReferral("eq.$userId")
        Log.d(TAG, "Fetched referrals: ${response.size}")
        Result.success(response)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching referrals", e)
        Result.failure(e)
    }

    /** دالة تضيف إحالة جديدة */
    suspend fun insertReferral(request: Referral): Boolean {
        val response = ReffrealRetrofitInstance.api.insertReferral(request)
        return response.isSuccessful
    }
}