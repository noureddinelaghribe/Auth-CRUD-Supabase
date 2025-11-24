package com.example.supabase.ViewModel.User

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.supabase.Repository.User.UserRepository
import com.example.supabase.data.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserRepository(application.applicationContext)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    /** دالة تجلب جميع المستخدمين من السيرفر */
    fun fetchUsers() {
        setLoading(true)
        viewModelScope.launch {
            repo.getUsers()
                .onSuccess { userList ->
                    _users.value = userList
                    _error.value = null
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "حدث خطأ غير متوقع"
                }
            setLoading(false)
        }
    }

    /** دالة تضيف مستخدم جديد ثم تعيد تحميل القائمة */
    fun addUser(user: User) {
        setLoading(true)
        viewModelScope.launch {
            repo.addUser(user)
                .onSuccess { fetchUsers() }
                .onFailure { exception ->
                    _error.value = exception.message ?: "فشل في إضافة المستخدم"
                    setLoading(false)
                }
        }
    }

    /** دالة تحدّث بيانات مستخدم */
    fun updateUser(uid: String, user: User) {
        setLoading(true)
        viewModelScope.launch {
            repo.updateUser(uid, user)
                .onSuccess { fetchUsers() }
                .onFailure { exception ->
                    _error.value = exception.message ?: "فشل في تحديث المستخدم"
                    setLoading(false)
                }
        }
    }

    /** دالة تحذف مستخدم محدد */
    fun deleteUser(uid: String) {
        setLoading(true)
        viewModelScope.launch {
            repo.deleteUser(uid)
                .onSuccess { fetchUsers() }
                .onFailure { exception ->
                    _error.value = exception.message ?: "فشل في حذف المستخدم"
                    setLoading(false)
                }
        }
    }

    /** دالة تنظف رسالة الخطأ المعروضة */
    fun clearError() {
        _error.value = null
    }

    /** دالة مساعدة لضبط حالة التحميل */
    private fun setLoading(show: Boolean) {
        _loading.value = show
    }
}