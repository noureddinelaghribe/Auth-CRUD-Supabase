package com.example.supabase.Repository.Setting

import android.content.Context
import android.util.Log
import com.example.supabase.data.Setting
import com.example.supabase.utels.PreferencesHelper

class SettingRepository(context: Context) {

    companion object {
        private const val TAG = "SettingRepository"
    }

    private val prefsHelper = PreferencesHelper(context)

    init {
        SettingRetrofitInstance.initialize(context)
    }

    /** دالة تجلب إعدادات التطبيق وتخزنها إذا لزم الأمر */
    suspend fun getSettings(): Result<Setting> = try {
        val response = SettingRetrofitInstance.api.getSettings()
        if (response.isEmpty()) {
            throw IllegalStateException("لم يتم العثور على إعدادات")
        }
        val setting = response.first()
        Log.d(TAG, "Fetched: $setting")
        Result.success(setting)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching settings", e)
        Result.failure(e)
    }
}