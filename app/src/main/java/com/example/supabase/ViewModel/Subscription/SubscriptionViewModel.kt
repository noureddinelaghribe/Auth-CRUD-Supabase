package com.example.supabase.ViewModel.Subscription

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.supabase.Repository.Subscription.SubscriptionRepository
import com.example.supabase.data.Subscription
import kotlinx.coroutines.launch

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SubscriptionRepository(application.applicationContext)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _subscriptions = MutableLiveData<List<Subscription>>()
    val subscriptions: LiveData<List<Subscription>> = _subscriptions

    private val _successInsert = MutableLiveData<Boolean>()
    val successInsert: LiveData<Boolean> = _successInsert

    /** دالة تجلب اشتراكات المستخدم وتحدّث الواجهة */
    fun loadSubscriptions() {
        setLoading(true)
        viewModelScope.launch {
            try {
                repo.getSubscriptions()
                    .onSuccess { result ->
                        _subscriptions.value = result
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

    /** دالة تضيف اشتراك جديد */
    fun insertSubscription(request: Subscription) {
        setLoading(true)
        viewModelScope.launch {
            try {
                val result = repo.insertSubscription(request)
                _successInsert.value = result
                _error.value = if (result) null else "تعذر إضافة الاشتراك"
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