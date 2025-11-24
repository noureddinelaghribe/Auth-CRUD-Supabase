package com.example.supabase.Repository.History

import android.content.Context
import android.util.Log
import com.example.supabase.data.History
import com.example.supabase.data.HistoryRequest
import com.example.supabase.utels.PreferencesHelper

class HistoryRepository(context: Context) {

    companion object {
        private const val TAG = "HistoryRepository"
    }

    private val prefsHelper = PreferencesHelper(context)

    init {
        HistoryRetrofitInstance.initialize(context)
    }

    /** دالة تجلب السجل الخاص بالمستخدم المسجل */
    suspend fun getHistory(): Result<List<History>> = try {
        val userId = prefsHelper.getUserId().orEmpty()
        val response = HistoryRetrofitInstance.api.getHistory("eq.$userId")
        Log.d(TAG, "Fetched history count: ${response.size}")
        Result.success(response)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching history", e)
        Result.failure(e)
    }

    /** دالة تضيف سجل جديد للمستخدم */
    suspend fun insertHistory(request: HistoryRequest): Boolean {
        val response = HistoryRetrofitInstance.api.insertHistory(request)
        return response.isSuccessful
    }

    /** دالة تحدث سجل محدد بالاعتماد على المعرف */
    suspend fun updateHistory(id: Long, request: HistoryRequest): Boolean {
        val response = HistoryRetrofitInstance.api.updateHistory("eq.$id", request)
        return response.isSuccessful
    }

    /** دالة تحذف سجل معين */
    suspend fun deleteHistory(id: Long): Boolean {
        val response = HistoryRetrofitInstance.api.deleteHistory("eq.$id")
        return response.isSuccessful
    }
}