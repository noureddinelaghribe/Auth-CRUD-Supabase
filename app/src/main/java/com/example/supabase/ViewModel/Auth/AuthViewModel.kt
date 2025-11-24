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
import com.example.supabase.utels.PreferencesHelper
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application.applicationContext)
    private val prefsHelper = PreferencesHelper(application.applicationContext)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _result = MutableLiveData<AuthResult?>()
    val result: LiveData<AuthResult?> = _result

    private val _userEmail = MutableLiveData<String?>()
    val userEmail: LiveData<String?> = _userEmail

    init {
        _userEmail.value = prefsHelper.getEmail()
    }

    /** دالة مسؤولة عن طلب تسجيل مستخدم جديد */
    fun signUp(email: String, password: String) {
        if (!validateInput(email, password)) return

        setLoading(true)
        viewModelScope.launch {
            _result.value = repository.signUp(email, password)
            setLoading(false)
        }
    }

    /** دالة مسؤولة عن تسجيل الدخول وتحديث البريد المخزن */
    fun signIn(email: String, password: String) {
        if (!validateInput(email, password)) return

        setLoading(true)
        viewModelScope.launch {
            val result = repository.signIn(email, password)
            _result.value = result
            if (result is AuthResult.Success) {
                _userEmail.value = prefsHelper.getEmail()
            }
            setLoading(false)
        }
    }

    /** دالة ترسل طلب إعادة ضبط كلمة المرور */
    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _result.value = AuthResult.Error("يرجى إدخال البريد الإلكتروني")
            return
        }

        setLoading(true)
        viewModelScope.launch {
            _result.value = repository.resetPassword(email)
            setLoading(false)
        }
    }

    /** دالة تنفذ عملية تسجيل الخروج من خلال المستودع */
    fun signOut() {
        setLoading(true)
        viewModelScope.launch {
            _result.value = repository.signOut()
            _userEmail.value = null
            setLoading(false)
        }
    }

    /** دالة تهيئ النتيجة كي لا يتم معالجتها مرتين */
    fun clearResult() {
        _result.value = null
    }

    /** دالة تفحص حالة المصادقة عند الحاجة */
    fun checkAuth(): AuthState {
        return repository.checkAuthOnLaunch()
    }

    /** دالة خاصة تتحقق من صحة البريد وكلمة المرور */
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

    /** دالة مساعدة لضبط حالة التحميل بشكل موحد */
    private fun setLoading(show: Boolean) {
        _loading.value = show
    }
}