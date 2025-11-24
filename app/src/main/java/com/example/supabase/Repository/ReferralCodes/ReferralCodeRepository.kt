package com.example.supabase.Repository.ReferralCodes

import android.content.Context
import android.util.Log
import com.example.supabase.data.ReferralCode

class ReferralCodeRepository(context: Context) {

    companion object {
        private const val TAG = "ReferralCodeRepository"
    }

    init {
        ReferralCodeRetrofitInstance.initialize()
        ReferralCodeRetrofitInstanceInsert.initialize(context)
    }

    /** دالة تجلب بيانات كود الإحالة المطلوب */
    suspend fun getReferralCode(code: String): Result<List<ReferralCode>> = try {
        val response = ReferralCodeRetrofitInstance.api.getReferralCode("eq.$code")
        if (response.isEmpty()) {
            throw IllegalStateException("لا يوجد كود مطابق")
        }
        Log.d(TAG, "Fetched: $response")
        Result.success(response)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching referral code", e)
        Result.failure(e)
    }

    /** دالة تضيف كود إحالة جديد */
    suspend fun insertReferralCode(referralCode: ReferralCode): Result<Boolean> = try {
        val response = ReferralCodeRetrofitInstanceInsert.api.insertReferralCode(referralCode)
        if (response.isSuccessful) {
            Result.success(true)
        } else {
            Log.e(TAG, "Insert failed: ${response.errorBody()?.string()}")
            Result.success(false)
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error inserting referral code", e)
        Result.failure(e)
    }
}