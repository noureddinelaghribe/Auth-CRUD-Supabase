package com.example.supabase.Repository.Auth


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.supabase.data.AuthResponse
import com.example.supabase.data.ErrorResponse
import com.example.supabase.data.ResetPasswordRequest
import com.example.supabase.data.SignInRequest
import com.example.supabase.data.SignUpRequest
import com.google.gson.Gson

// نتائج المصادقة
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

// حالة المصادقة
sealed class AuthState {
    object LoggedIn : AuthState()
    object LoggedOut : AuthState()
}

class AuthRepository(context: Context) {

    private val apiService = AuthRetrofitInstance.apiService
    private val prefs: SharedPreferences =
        context.getSharedPreferences("supabase_auth", Context.MODE_PRIVATE)

    // حفظ بيانات الجلسة
    private fun saveSession(response: AuthResponse) {
        prefs.edit().apply {
            putString("access_token", response.access_token)
            putString("refresh_token", response.refresh_token)
            putString("user_email", response.user?.email)
            putString("user_id", response.user?.id)
            apply()
        }
    }

    // حذف بيانات الجلسة
    private fun clearSession() {
        prefs.edit().clear().apply()
    }

    // التحقق من حالة تسجيل الدخول
    fun isLoggedIn(): Boolean {
        return prefs.getString("access_token", null) != null
    }

    // الحصول على البريد الإلكتروني المحفوظ
    fun getUserEmail(): String? {
        return prefs.getString("user_email", null)
    }

    // التسجيل
    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            val request = SignUpRequest(email, password)
            val response = apiService.signUp(AuthRetrofitInstance.API_KEY, request)

            if (response.isSuccessful && response.body() != null) {
                Log.d("AuthRepository", "تم التسجيل بنجاح")
                AuthResult.Success
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "خطأ في التسجيل: $errorBody")
                val errorMsg = parseError(errorBody)
                AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "خطأ في التسجيل", e)
            AuthResult.Error("حدث خطأ: ${e.localizedMessage}")
        }
    }

    // تسجيل الدخول
    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val request = SignInRequest(email, password)
            val response = apiService.signIn(AuthRetrofitInstance.API_KEY, request)

            if (response.isSuccessful && response.body() != null) {
                saveSession(response.body()!!)
                Log.d("AuthRepository", "تم تسجيل الدخول بنجاح")
                AuthResult.Success
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "خطأ في تسجيل الدخول: $errorBody")
                val errorMsg = parseError(errorBody)
                AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "خطأ في تسجيل الدخول", e)
            AuthResult.Error("حدث خطأ: ${e.localizedMessage}")
        }
    }

    // إعادة تعيين كلمة المرور
    suspend fun resetPassword(email: String): AuthResult {
        return try {
            val request = ResetPasswordRequest(email)
            val response = apiService.resetPassword(AuthRetrofitInstance.API_KEY, request)

            if (response.isSuccessful) {
                Log.d("AuthRepository", "تم إرسال رابط إعادة تعيين كلمة المرور")
                AuthResult.Success
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "خطأ في إعادة تعيين كلمة المرور: $errorBody")
                val errorMsg = parseError(errorBody)
                AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "خطأ في إعادة تعيين كلمة المرور", e)
            AuthResult.Error("حدث خطأ: ${e.localizedMessage}")
        }
    }

    // تسجيل الخروج
    fun signOut(): AuthResult {
        return try {
            clearSession()
            Log.d("AuthRepository", "تم تسجيل الخروج بنجاح")
            AuthResult.Success
        } catch (e: Exception) {
            Log.e("AuthRepository", "خطأ في تسجيل الخروج", e)
            AuthResult.Error("حدث خطأ: ${e.localizedMessage}")
        }
    }

    // التحقق من الجلسة عند بدء التطبيق
    fun checkAuthOnLaunch(): AuthState {
        return if (isLoggedIn()) {
            AuthState.LoggedIn
        } else {
            AuthState.LoggedOut
        }
    }

    // تحليل رسالة الخطأ
    private fun parseError(errorBody: String?): String {
        return try {
            if (errorBody != null) {
                val error = Gson().fromJson(errorBody, ErrorResponse::class.java)
                error.error_description ?: error.message ?: error.error ?: "حدث خطأ غير معروف"
            } else {
                "حدث خطأ غير معروف"
            }
        } catch (e: Exception) {
            "حدث خطأ غير معروف"
        }
    }
}