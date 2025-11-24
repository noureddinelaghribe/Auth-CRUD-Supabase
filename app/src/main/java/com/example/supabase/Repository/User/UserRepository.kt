package com.example.supabase.Repository.User

import android.content.Context
import android.util.Log
import com.example.supabase.data.User

class UserRepository(context: Context) {

    companion object {
        private const val TAG = "UserRepository"
    }

    init {
        UserRetrofitInstance.initialize(context)
    }

    /** دالة تجلب قائمة المستخدمين بالكامل */
    suspend fun getUsers(): Result<List<User>> = try {
        val response = UserRetrofitInstance.api.getUsers()
        Log.d(TAG, "Fetched users: ${response.size}")
        Result.success(response)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching users", e)
        Result.failure(e)
    }

    /** دالة تضيف مستخدم جديد */
    suspend fun addUser(user: User): Result<List<User>> = try {
        val response = UserRetrofitInstance.api.insertUser(user)
        if (response.isSuccessful) {
            Result.success(response.body() ?: emptyList())
        } else {
            Result.failure(Exception("رمز الخطأ: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** دالة تحدث بيانات مستخدم محدد */
    suspend fun updateUser(uid: String, user: User): Result<List<User>> = try {
        val response = UserRetrofitInstance.api.updateUser("eq.$uid", user)
        if (response.isSuccessful) {
            Result.success(response.body() ?: emptyList())
        } else {
            Result.failure(Exception("رمز الخطأ: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** دالة تحذف مستخدم */
    suspend fun deleteUser(uid: String): Result<Unit> = try {
        val response = UserRetrofitInstance.api.deleteUser("eq.$uid")
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("رمز الخطأ: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}