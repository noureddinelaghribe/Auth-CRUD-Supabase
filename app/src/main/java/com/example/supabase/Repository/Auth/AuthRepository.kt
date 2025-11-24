package com.example.supabase.Repository.Auth

import android.content.Context
import android.util.Log
import com.example.supabase.data.ErrorResponse
import com.example.supabase.data.ResetPasswordRequest
import com.example.supabase.data.SignInRequest
import com.example.supabase.data.SignUpRequest
import com.example.supabase.utels.PreferencesHelper
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

    companion object {
        private const val TAG = "AuthRepository"
    }

    private val apiService = AuthRetrofitInstance.apiService
    private val prefsHelper = PreferencesHelper(context)

    /** دالة مسؤولة عن إرسال طلب تسجيل مستخدم جديد وإرجاع الحالة */
    suspend fun signUp(email: String, password: String): AuthResult = try {
        val request = SignUpRequest(email, password)
        val response = apiService.signUp(AuthRetrofitInstance.API_KEY, request)

        if (response.isSuccessful && response.body() != null) {
            prefsHelper.saveSession(response.body()!!)
            Log.d(TAG, "تم التسجيل بنجاح")
            AuthResult.Success
        } else {
            val errorMsg = parseError(response.errorBody()?.string())
            Log.e(TAG, "خطأ في التسجيل: $errorMsg")
            AuthResult.Error(errorMsg)
        }
    } catch (e: Exception) {
        Log.e(TAG, "خطأ في التسجيل", e)
        AuthResult.Error("حدث خطأ: ${e.localizedMessage}")
    }

    /** دالة مسؤولة عن تسجيل الدخول والتحقق من بيانات الاعتماد */
    suspend fun signIn(email: String, password: String): AuthResult = try {
        val request = SignInRequest(email, password)
        val response = apiService.signIn(AuthRetrofitInstance.API_KEY, request)

        if (response.isSuccessful && response.body() != null) {
            prefsHelper.saveSession(response.body()!!)
            Log.d(TAG, "تم تسجيل الدخول بنجاح")
            AuthResult.Success
        } else {
            val errorMsg = parseError(response.errorBody()?.string())
            Log.e(TAG, "خطأ في تسجيل الدخول: $errorMsg")
            AuthResult.Error(errorMsg)
        }
    } catch (e: Exception) {
        Log.e(TAG, "خطأ في تسجيل الدخول", e)
        AuthResult.Error("حدث خطأ: ${e.localizedMessage}")
    }

    /** دالة ترسل طلب إعادة تعيين كلمة المرور */
    suspend fun resetPassword(email: String): AuthResult = try {
        val request = ResetPasswordRequest(email)
        val response = apiService.resetPassword(AuthRetrofitInstance.API_KEY, request)

        if (response.isSuccessful) {
            Log.d(TAG, "تم إرسال رابط إعادة تعيين كلمة المرور")
            AuthResult.Success
        } else {
            val errorMsg = parseError(response.errorBody()?.string())
            Log.e(TAG, "خطأ في إعادة تعيين كلمة المرور: $errorMsg")
            AuthResult.Error(errorMsg)
        }
    } catch (e: Exception) {
        Log.e(TAG, "خطأ في إعادة تعيين كلمة المرور", e)
        AuthResult.Error("حدث خطأ: ${e.localizedMessage}")
    }

    /** دالة تحذف بيانات الجلسة وتعيد نتيجة تسجيل الخروج */
    fun signOut(): AuthResult = try {
        prefsHelper.clearSession()
        Log.d(TAG, "تم تسجيل الخروج بنجاح")
        AuthResult.Success
    } catch (e: Exception) {
        Log.e(TAG, "خطأ في تسجيل الخروج", e)
        AuthResult.Error("حدث خطأ: ${e.localizedMessage}")
    }

    /** دالة تتحقق من حالة الجلسة عند تشغيل التطبيق */
    fun checkAuthOnLaunch(): AuthState = if (prefsHelper.isAccessTokenExcest()) {
        AuthState.LoggedIn
    } else {
        AuthState.LoggedOut
    }

    /** دالة خاصة لتحليل رسالة الخطأ القادمة من الخادم */
    private fun parseError(errorBody: String?): String = try {
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
