package com.example.supabase.Repository.User

import com.example.supabase.data.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApiService {

    @GET("rest/v1/users")
    suspend fun getUsers(): List<User>

    @POST("rest/v1/users")
    suspend fun insertUser(
        @Body user: User
    ): Response<Unit>

    @PATCH("rest/v1/users")
    suspend fun updateUser(
        @Query("user_id") userId: String,
        @Body user: User
    ): Response<Unit>

    @DELETE("rest/v1/users")
    suspend fun deleteUser(
        @Query("user_id") userId: String
    ): Response<Unit>
}
