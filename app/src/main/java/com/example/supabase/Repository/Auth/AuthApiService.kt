package com.example.supabase.Repository.Auth


import com.example.supabase.data.AuthResponse
import com.example.supabase.data.ResetPasswordRequest
import com.example.supabase.data.SignInRequest
import com.example.supabase.data.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


// واجهة Retrofit API
interface AuthApiService {

    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Body request: SignUpRequest
    ): Response<AuthResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Header("apikey") apiKey: String,
        @Body request: SignInRequest
    ): Response<AuthResponse>

    @POST("auth/v1/recover")
    suspend fun resetPassword(
        @Header("apikey") apiKey: String,
        @Body request: ResetPasswordRequest
    ): Response<Unit>
}