package com.example.supabase.Repository.UserDevice

import android.content.Context
import android.util.Log
import com.example.supabase.data.UserDevice

class UserDevicesRepository(context: Context) {

    companion object {
        private const val TAG = "UserDevicesRepo"
    }

    init {
        UserDeviceRetrofitInstance.initialize()
        UserDeviceRetrofitInstanceInsert.initialize(context)
    }

    /** دالة تتحقق من وجود جهاز مرتبط بالكود المحدد */
    suspend fun hasUserDevice(code: String): Result<Boolean> = try {
        val response = UserDeviceRetrofitInstance.api.getUserDevice("eq.$code")
        Log.d(TAG, "Fetched devices: ${response.size}")
        Result.success(response.isNotEmpty())
    } catch (e: Exception) {
        Log.e(TAG, "Error checking device", e)
        Result.failure(e)
    }

    /** دالة تضيف جهاز مستخدم جديد */
    suspend fun insertUserDevice(device: UserDevice): Result<Boolean> = try {
        val response = UserDeviceRetrofitInstanceInsert.api.insertUserDevice(device)
        if (response.isSuccessful) {
            Result.success(true)
        } else {
            Log.e(TAG, "Insert failed: ${response.errorBody()?.string()}")
            Result.success(false)
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error inserting device", e)
        Result.failure(e)
    }
}