package com.example.supabase.ViewModel.Setting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.supabase.Repository.Setting.SettingRepository
import com.example.supabase.data.Setting
import kotlinx.coroutines.launch

class SettingViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SettingRepository(application.applicationContext)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _settings = MutableLiveData<Setting>()
    val settings: LiveData<Setting> = _settings

    /** دالة تجلب إعدادات التطبيق من المستودع */
    fun fetchSettings() {
        setLoading(true)
        viewModelScope.launch {
            repo.getSettings()
                .onSuccess { setting ->
                    _settings.value = setting
                    _error.value = null
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "حدث خطأ غير متوقع"
                }
            setLoading(false)
        }
    }

    /** دالة مساعدة لضبط حالة التحميل */
    private fun setLoading(show: Boolean) {
        _loading.value = show
    }
}