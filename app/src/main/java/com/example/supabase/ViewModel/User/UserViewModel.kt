package com.example.supabase.ViewModel.User

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supabase.Repository.User.UserRepository
import com.example.supabase.data.User
import kotlinx.coroutines.launch

class UserViewModel (application: Application) : AndroidViewModel(application) {

    private val repo = UserRepository(application.applicationContext)
    val users = MutableLiveData<List<User>>()

    fun fetchUsers() {
        viewModelScope.launch {
            users.value = repo.getUsers()
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            repo.addUser(user)
            fetchUsers()
        }
    }

    fun updateUser(uid: String, user: User) {
        viewModelScope.launch {
            repo.updateUser(uid, user)
            fetchUsers()
        }
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {
            repo.deleteUser(uid)
            fetchUsers()
        }
    }
}
