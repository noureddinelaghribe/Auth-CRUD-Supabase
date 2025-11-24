package com.example.supabase.ViewModel.Referral

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.supabase.Repository.Reffreal.ReffrealRepository
import com.example.supabase.data.Referral
import kotlinx.coroutines.launch

class ReferralViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ReffrealRepository(application.applicationContext)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _referrals = MutableLiveData<List<Referral>>()
    val referrals: LiveData<List<Referral>> = _referrals

    private val _successInsert = MutableLiveData<Boolean>()
    val successInsert: LiveData<Boolean> = _successInsert

    /** دالة تجلب جميع الإحالات الخاصة بالمستخدم */
    fun loadReferrals() {
        setLoading(true)
        viewModelScope.launch {
            try {
                repo.getReferrals()
                    .onSuccess { result ->
                        _referrals.value = result
                        _error.value = null
                    }
                    .onFailure { exception ->
                        _error.value = exception.message ?: "حدث خطأ غير متوقع"
                    }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                setLoading(false)
            }
        }
    }

    /** دالة تضيف إحالة جديدة */
    fun insertReferral(request: Referral) {
        setLoading(true)
        viewModelScope.launch {
            try {
                val result = repo.insertReferral(request)
                _successInsert.value = result
                _error.value = if (result) null else "تعذر حفظ الإحالة"
            } catch (e: Exception) {
                _error.value = e.message
                _successInsert.value = false
            } finally {
                setLoading(false)
            }
        }
    }

    /** دالة مساعدة لضبط حالة التحميل */
    private fun setLoading(show: Boolean) {
        _loading.value = show
    }
}
