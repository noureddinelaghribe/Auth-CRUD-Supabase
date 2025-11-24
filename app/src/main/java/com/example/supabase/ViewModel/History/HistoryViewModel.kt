package com.example.supabase.ViewModel.History

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.supabase.Repository.History.HistoryRepository
import com.example.supabase.data.History
import com.example.supabase.data.HistoryRequest
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = HistoryRepository(application.applicationContext)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _history = MutableLiveData<List<History>>()
    val history: LiveData<List<History>> = _history

    private val _successInsert = MutableLiveData<Boolean>()
    val successInsert: LiveData<Boolean> = _successInsert

    private val _successDelete = MutableLiveData<Boolean>()
    val successDelete: LiveData<Boolean> = _successDelete

    private val _successUpdate = MutableLiveData<Boolean>()
    val successUpdate: LiveData<Boolean> = _successUpdate

    /** دالة تجلب السجلات وتحدّث الحالة */
    fun loadHistory() {
        setLoading(true)
        viewModelScope.launch {
            try {
                repo.getHistory()
                    .onSuccess { list ->
                        _history.value = list
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

    /** دالة تضيف سجل جديد */
    fun insertHistory(request: HistoryRequest) {
        setLoading(true)
        viewModelScope.launch {
            try {
                val result = repo.insertHistory(request)
                _successInsert.value = result
                _error.value = if (result) null else "تعذر حفظ السجل"
            } catch (e: Exception) {
                _error.value = e.message
                _successInsert.value = false
            } finally {
                setLoading(false)
            }
        }
    }

    /** دالة تحذف سجل موجود */
    fun deleteHistory(id: Long) {
        setLoading(true)
        viewModelScope.launch {
            try {
                val result = repo.deleteHistory(id)
                _successDelete.value = result
                _error.value = if (result) null else "تعذر حذف السجل"
            } catch (e: Exception) {
                _error.value = e.message
                _successDelete.value = false
            } finally {
                setLoading(false)
            }
        }
    }

    /** دالة تحدّث بيانات سجل محدد */
    fun updateHistory(id: Long, request: HistoryRequest) {
        setLoading(true)
        viewModelScope.launch {
            try {
                val result = repo.updateHistory(id, request)
                _successUpdate.value = result
                _error.value = if (result) null else "تعذر تحديث السجل"
            } catch (e: Exception) {
                _error.value = e.message
                _successUpdate.value = false
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
