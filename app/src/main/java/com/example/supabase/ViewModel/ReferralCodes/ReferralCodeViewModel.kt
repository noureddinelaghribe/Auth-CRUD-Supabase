package com.example.supabase.ViewModel.ReferralCodes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.supabase.Repository.ReferralCodes.ReferralCodeRepository
import com.example.supabase.data.ReferralCode
import kotlinx.coroutines.launch

class ReferralCodeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ReferralCodeRepository(application)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _referralCodeResult = MutableLiveData<List<ReferralCode>>()
    val referralCodeResult: LiveData<List<ReferralCode>> = _referralCodeResult

    private val _insertSuccess = MutableLiveData<Boolean>()
    val insertSuccess: LiveData<Boolean> = _insertSuccess

    /** دالة تتحقق من كود الإحالة المدخل */
    fun validateReferralCode(code: String) {
        if (code.isBlank() || code.length != 6) {
            _referralCodeResult.value = emptyList()
            return
        }
        setLoading(true)
        viewModelScope.launch {
            repo.getReferralCode(code)
                .onSuccess { result ->
                    _referralCodeResult.value = result
                    _error.value = null
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "كود الإحالة غير صالح"
                }
            setLoading(false)
        }
    }

    /** دالة تضيف كود إحالة جديد */
    fun insertReferralCode(referralCode: ReferralCode) {
        setLoading(true)
        viewModelScope.launch {
            repo.insertReferralCode(referralCode)
                .onSuccess { success ->
                    _insertSuccess.value = success
                    _error.value = if (success) null else "تعذر حفظ كود الإحالة"
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "خطأ أثناء إدخال الكود"
                    _insertSuccess.value = false
                }
            setLoading(false)
        }
    }

    /** دالة مساعدة لضبط حالة التحميل */
    private fun setLoading(show: Boolean) {
        _loading.value = show
    }
}