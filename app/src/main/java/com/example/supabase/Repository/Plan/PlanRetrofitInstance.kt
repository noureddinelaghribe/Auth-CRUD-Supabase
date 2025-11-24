package com.example.supabase.Repository.Plan



import android.content.Context
import com.example.supabase.utels.PreferencesHelper
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



object PlanRetrofitInstance {

    private const val BASE_URL = "https://jdgmxwtfnxxmnmiqaawd.supabase.co/" // e.g., https://xxx.supabase.co/
    const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImpkZ214d3Rmbnh4bW5taXFhYXdkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEyMjgwMjksImV4cCI6MjA3NjgwNDAyOX0.5QgQ-nV38oigRe0IxPMBk9J9QjATwO8EZ6QegZAGIkM"

    private var retrofit: Retrofit? = null
    lateinit var api: PlanApiService

    fun initialize() {

        // Configure Gson with UTF-8 support for Arabic
        val gson = GsonBuilder()
            .setLenient()
            .create()

        // Logging interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Auth interceptor - adds JWT token to all requests
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Accept-Charset", "utf-8")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        api = retrofit!!.create(PlanApiService::class.java)
    }
}