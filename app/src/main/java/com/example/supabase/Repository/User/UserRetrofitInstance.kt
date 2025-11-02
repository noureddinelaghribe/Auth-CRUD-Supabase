package com.example.supabase.Repository.User

import android.content.Context
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserRetrofitInstance {

    private const val BASE_URL = "https://jdgmxwtfnxxmnmiqaawd.supabase.co/"
    const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImpkZ214d3Rmbnh4bW5taXFhYXdkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEyMjgwMjksImV4cCI6MjA3NjgwNDAyOX0.5QgQ-nV38oigRe0IxPMBk9J9QjATwO8EZ6QegZAGIkM"
    private const val PREFS_NAME = "supabase_auth"
    private const val AUTH_TOKEN_KEY = "access_token"

    private var authToken: String? = null

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        authToken = prefs.getString(AUTH_TOKEN_KEY, null)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $authToken")
                .method(original.method, original.body)

            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()

    val api: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(UserApiService::class.java)
    }
}
