package com.example.supabase.ViewModel.UserDevice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.supabase.Repository.UserDevice.UserDevicesRepository
import com.example.supabase.data.UserDevice
import kotlinx.coroutines.launch

class UserDeviceViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserDevicesRepository(application.applicationContext)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isExist = MutableLiveData<Boolean>()
    val isExist: LiveData<Boolean> = _isExist

    private val _insertSuccess = MutableLiveData<Boolean>()
    val insertSuccess: LiveData<Boolean> = _insertSuccess

    /** دالة تفحص إذا كان الجهاز مسجلاً مسبقاً */
    fun checkUserDevice(code: String) {
        setLoading(true)
        viewModelScope.launch {
            repo.hasUserDevice(code)
                .onSuccess { exists ->
                    _isExist.value = exists
                    _error.value = null
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "حدث خطأ غير متوقع"
                }
            setLoading(false)
        }
    }

    /** دالة تضيف جهاز جديد ثم تحدّث حالة النجاح */
    fun insertUserDevice(device: UserDevice) {
        setLoading(true)
        viewModelScope.launch {
            repo.insertUserDevice(device)
                .onSuccess { success ->
                    _insertSuccess.value = success
                    _error.value = if (success) null else "تعذر حفظ الجهاز"
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "تعذر حفظ الجهاز"
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