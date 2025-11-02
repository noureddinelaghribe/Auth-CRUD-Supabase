package com.example.supabase.ViewModel.Auth

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.supabase.Repository.Auth.AuthRepository
import com.example.supabase.Repository.Auth.AuthResult
import com.example.supabase.Repository.Auth.AuthState
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application.applicationContext)

    // حالة التحميل
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // نتيجة العملية
    private val _result = MutableLiveData<AuthResult?>()
    val result: LiveData<AuthResult?> = _result

    // البريد الإلكتروني للمستخدم
    private val _userEmail = MutableLiveData<String?>()
    val userEmail: LiveData<String?> = _userEmail

    init {
        _userEmail.value = repository.getUserEmail()
    }

    // التسجيل
    fun signUp(email: String, password: String) {
        if (!validateInput(email, password)) return

        _loading.value = true
        viewModelScope.launch {
            val r = repository.signUp(email, password)
            _result.value = r
            _loading.value = false
        }
    }

    // تسجيل الدخول
    fun signIn(email: String, password: String) {
        if (!validateInput(email, password)) return

        _loading.value = true
        viewModelScope.launch {
            val r = repository.signIn(email, password)
            _result.value = r
            _loading.value = false
            if (r is AuthResult.Success) {
                _userEmail.value = repository.getUserEmail()
            }
        }
    }

    // إعادة تعيين كلمة المرور
    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _result.value = AuthResult.Error("يرجى إدخال البريد الإلكتروني")
            return
        }

        _loading.value = true
        viewModelScope.launch {
            val r = repository.resetPassword(email)
            _result.value = r
            _loading.value = false
        }
    }

    // تسجيل الخروج
    fun signOut() {
        _loading.value = true
        viewModelScope.launch {
            val r = repository.signOut()
            _result.value = r
            _userEmail.value = null
            _loading.value = false
        }
    }

    // مسح النتيجة
    fun clearResult() {
        _result.value = null
    }

    // التحقق من المصادقة
    fun checkAuth(): AuthState {
        return repository.checkAuthOnLaunch()
    }

    // التحقق من صحة المدخلات
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isBlank()) {
            _result.value = AuthResult.Error("يرجى إدخال البريد الإلكتروني")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _result.value = AuthResult.Error("البريد الإلكتروني غير صحيح")
            return false
        }

        if (password.isBlank()) {
            _result.value = AuthResult.Error("يرجى إدخال كلمة المرور")
            return false
        }

        if (password.length < 6) {
            _result.value = AuthResult.Error("كلمة المرور يجب أن تكون 6 أحرف على الأقل")
            return false
        }

        return true
    }
}