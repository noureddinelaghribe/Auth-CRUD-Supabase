package com.example.supabase.utels


import android.content.Context
import android.content.SharedPreferences
import com.example.supabase.data.ApiItem
import com.example.supabase.data.AuthResponse
import com.example.supabase.data.Setting
import com.google.gson.Gson
import kotlin.apply

class PreferencesHelper(context: Context) {
    private val prefsAuth: SharedPreferences =
        context.getSharedPreferences("supabase_auth", Context.MODE_PRIVATE)

    private val prefsSettings: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)



    fun saveAccessToken(token: String) {
        prefsAuth.edit().putString("access_token", token).apply()
    }

    fun getAccessToken(): String? {
        return prefsAuth.getString("access_token", null)
    }

    fun clearAccessToken() {
        prefsAuth.edit().remove("access_token").apply()
    }



    fun saveRefreshToken(token: String) {
        prefsAuth.edit().putString("refresh_token", token).apply()
    }

    fun getRefreshToken(): String? {
        return prefsAuth.getString("refresh_token", null)
    }

    fun clearRefreshToken() {
        prefsAuth.edit().remove("refresh_token").apply()
    }



    fun saveEmail(token: String) {
        prefsAuth.edit().putString("user_email", token).apply()
    }

    fun getEmail(): String? {
        return prefsAuth.getString("user_email", null)
    }

    fun clearEmail() {
        prefsAuth.edit().remove("user_email").apply()
    }




    fun saveUserId(token: String) {
        prefsAuth.edit().putString("user_id", token).apply()
    }

    fun getUserId(): String? {
        return prefsAuth.getString("user_id", null)
    }

    fun clearUserId() {
        prefsAuth.edit().remove("user_id").apply()
    }


    // حفظ بيانات الجلسة
    fun saveSession(response: AuthResponse) {
        prefsAuth.edit().apply {
            putString("access_token", response.access_token)
            putString("refresh_token", response.refresh_token)
            putString("user_email", response.user?.email)
            putString("user_id", response.user?.id)
            apply()
        }
    }

    // التحقق من حالة تسجيل الدخول
    fun isAccessTokenExcest(): Boolean {
        return prefsAuth.getString("access_token", null) != null
    }

    fun clearSession() {
        prefsAuth.edit().clear().apply()
    }




    fun saveSetting(setting: Setting) {
        prefsSettings.edit().apply {
            putLong("id", setting.id)
            putString("created_at", setting.createdAt)
            putString("verstion", setting.verstion)
            putBoolean("updateMode", setting.updateMode)
            putString("updateUrl", setting.updateUrl)
            putBoolean("maintenanceMode", setting.maintenanceMode)
            putString("maintenanceMessage", setting.maintenanceMessage)

            // تحويل List<ApiItem> إلى JSON String
            val gson = Gson()
            val apisJsonString = gson.toJson(setting.apisJson)
            putString("apisJson", apisJsonString)

            apply()
        }
    }

    fun getSetting(): Setting {
        val id = prefsSettings.getLong("id", 0)?: 0
        val createdAt = prefsSettings.getString("created_at", "") ?: ""
        val verstion = prefsSettings.getString("verstion", "1.0.0") ?: "1.0.0"
        val updateMode = prefsSettings.getBoolean("updateMode", false)
        val updateUrl = prefsSettings.getString("updateUrl", "") ?: ""
        val maintenanceMode = prefsSettings.getBoolean("maintenanceMode", false)
        val maintenanceMessage = prefsSettings.getString("maintenanceMessage", "") ?: ""

        // قراءة apisJson وتحويله من JSON String إلى List<ApiItem>
        val gson = Gson()
        val apisJsonString = prefsSettings.getString("apisJson", "[]") ?: "[]"
        val apisJson: List<ApiItem> = gson.fromJson(
            apisJsonString,
            Array<ApiItem>::class.java
        ).toList()

        return Setting(
            id = id, // يمكنك تعديل id إذا أردت تخزينه أيضاً
            createdAt = createdAt, // يمكنك تعديل created_at إذا أردت تخزينه أيضاً
            verstion = verstion,
            updateMode = updateMode,
            updateUrl = updateUrl,
            maintenanceMode = maintenanceMode,
            maintenanceMessage = maintenanceMessage,
            apisJson = apisJson
        )
    }



    fun clearSettings() {
        prefsSettings.edit().clear().apply()
    }







}