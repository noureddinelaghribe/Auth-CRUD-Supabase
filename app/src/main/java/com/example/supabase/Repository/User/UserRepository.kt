package com.example.supabase.Repository.User

import android.content.Context
import com.example.supabase.data.User

class UserRepository(private val context: Context) {
    
    init {
        // Initialize the Retrofit instance with the auth token
        UserRetrofitInstance.initialize(context)
    }

    suspend fun getUsers() = UserRetrofitInstance.api.getUsers()

    suspend fun addUser(user: User) =
        UserRetrofitInstance.api.insertUser(user)

    suspend fun updateUser(uid: String, user: User) =
        UserRetrofitInstance.api.updateUser(uid, user)

    suspend fun deleteUser(uid: String) =
        UserRetrofitInstance.api.deleteUser(uid)
}
