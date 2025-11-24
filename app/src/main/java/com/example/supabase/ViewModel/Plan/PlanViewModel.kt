package com.example.supabase.ViewModel.Plan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.supabase.Repository.Plan.PlanRepository
import com.example.supabase.data.Plan
import kotlinx.coroutines.launch

class PlanViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PlanRepository()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _plans = MutableLiveData<List<Plan>>()
    val plans: LiveData<List<Plan>> = _plans

    /** دالة تجلب جميع الباقات وتحدّث الحالة */
    fun loadPlans() {
        setLoading(true)
        viewModelScope.launch {
            try {
                repo.getPlans()
                    .onSuccess { result ->
                        _plans.value = result
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

    /** دالة مساعدة لضبط حالة التحميل */
    private fun setLoading(show: Boolean) {
        _loading.value = show
    }
}