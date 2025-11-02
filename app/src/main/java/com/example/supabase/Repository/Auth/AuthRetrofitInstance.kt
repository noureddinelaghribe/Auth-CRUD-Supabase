package com.example.supabase.Repository.Auth

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AuthRetrofitInstance {

    private const val BASE_URL = "https://jdgmxwtfnxxmnmiqaawd.supabase.co/"
    const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImpkZ214d3Rmbnh4bW5taXFhYXdkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEyMjgwMjksImV4cCI6MjA3NjgwNDAyOX0.5QgQ-nV38oigRe0IxPMBk9J9QjATwO8EZ6QegZAGIkM"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: AuthApiService = retrofit.create(AuthApiService::class.java)
}