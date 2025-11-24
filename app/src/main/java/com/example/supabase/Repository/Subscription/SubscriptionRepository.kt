package com.example.supabase.Repository.Subscription

import android.content.Context
import android.util.Log
import com.example.supabase.data.Subscription
import com.example.supabase.utels.PreferencesHelper

class SubscriptionRepository(context: Context) {

    companion object {
        private const val TAG = "SubscriptionRepo"
    }

    private val prefsHelper = PreferencesHelper(context)

    init {
        SubscriptionRetrofitInstance.initialize(context)
    }

    /** دالة تجلب اشتراكات المستخدم الحالي */
    suspend fun getSubscriptions(): Result<List<Subscription>> = try {
        val userId = prefsHelper.getUserId().orEmpty()
        val response = SubscriptionRetrofitInstance.api.getSubscriptions("eq.$userId")
        Log.d(TAG, "Fetched subscriptions: ${response.size}")
        Result.success(response)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching subscriptions", e)
        Result.failure(e)
    }

    /** دالة تضيف اشتراك جديد للمستخدم */
    suspend fun insertSubscription(request: Subscription): Boolean {
        val response = SubscriptionRetrofitInstance.api.insertSubscription(request)
        return response.isSuccessful
    }
}