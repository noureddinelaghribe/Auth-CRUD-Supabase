//package com.example.supabase.Repository.User
//
//import android.content.Context
//import android.content.SharedPreferences
//import okhttp3.OkHttpClient
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object UserRetrofitInstance {
//
//    private const val BASE_URL = "https://jdgmxwtfnxxmnmiqaawd.supabase.co/"
//    const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImpkZ214d3Rmbnh4bW5taXFhYXdkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEyMjgwMjksImV4cCI6MjA3NjgwNDAyOX0.5QgQ-nV38oigRe0IxPMBk9J9QjATwO8EZ6QegZAGIkM"
//    private const val PREFS_NAME = "supabase_auth"
//    private const val AUTH_TOKEN_KEY = "access_token"
//
//    private var authToken: String? = null
//
//    fun initialize(context: Context) {
//        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        authToken = prefs.getString(AUTH_TOKEN_KEY, null)
//    }
//
//    private val client = OkHttpClient.Builder()
//        .addInterceptor { chain ->
//            val original = chain.request()
//            val requestBuilder = original.newBuilder()
//                .addHeader("apikey", API_KEY)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization", "Bearer $authToken")
//                .method(original.method, original.body)
//
//            val request = requestBuilder.build()
//            chain.proceed(request)
//        }.build()
//
//    val api: SettingApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
//            .build()
//            .create(SettingApiService::class.java)
//    }
//}



package com.example.supabase.Repository.User

import android.content.Context
import com.example.supabase.utels.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

object UserRetrofitInstance {

    private const val BASE_URL = "https://jdgmxwtfnxxmnmiqaawd.supabase.co/" // e.g., https://xxx.supabase.co/
    const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImpkZ214d3Rmbnh4bW5taXFhYXdkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEyMjgwMjksImV4cCI6MjA3NjgwNDAyOX0.5QgQ-nV38oigRe0IxPMBk9J9QjATwO8EZ6QegZAGIkM"

    private var retrofit: Retrofit? = null
    lateinit var api: UserApiService

    fun initialize(context: Context) {
        val prefsHelper = PreferencesHelper(context)

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
            val token = prefsHelper.getAccessToken() // Get JWT from SharedPreferences
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
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

        api = retrofit!!.create(UserApiService::class.java)
    }
}