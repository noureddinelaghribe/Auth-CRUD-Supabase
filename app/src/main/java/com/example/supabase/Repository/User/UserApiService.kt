//package com.example.supabase.Repository.User
//
//import com.example.supabase.data.User
//import retrofit2.Response
//import retrofit2.http.Body
//import retrofit2.http.DELETE
//import retrofit2.http.GET
//import retrofit2.http.PATCH
//import retrofit2.http.POST
//import retrofit2.http.Query
//
//interface SettingApiService {
//
//    @GET("rest/v1/users")
//    suspend fun getUsers(): List<User>
//
//    @POST("rest/v1/users")
//    suspend fun insertUser(
//        @Body user: User
//    ): Response<Unit>
//
//    @PATCH("rest/v1/users")
//    suspend fun updateUser(
//        @Query("user_id") userId: String,
//        @Body user: User
//    ): Response<Unit>
//
//    @DELETE("rest/v1/users")
//    suspend fun deleteUser(
//        @Query("user_id") userId: String
//    ): Response<Unit>
//}





package com.example.supabase.Repository.User

import com.example.supabase.data.User
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {

    @GET("rest/v1/users")
    suspend fun getUsers(
        @Query("select") select: String = "*"
    ): List<User>

    @POST("rest/v1/users")
    @Headers("Prefer: return=representation")
    suspend fun insertUser(
        @Body user: User
    ): Response<List<User>>

    @PATCH("rest/v1/users")
    @Headers("Prefer: return=representation")
    suspend fun updateUser(
        @Query("user_id") filter: String, // Will be "eq.{userId}"
        @Body user: User
    ): Response<List<User>>

    @DELETE("rest/v1/users")
    suspend fun deleteUser(
        @Query("user_id") filter: String // Will be "eq.{userId}"
    ): Response<Unit>
}