package com.example.supabase.Repository.Subscription

import com.example.supabase.data.Plan
import com.example.supabase.data.Subscription
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface SubscriptionApiService {

    @GET("rest/v1/subscribeptions")
    suspend fun getSubscriptions(@Query("user_id") userId: String): List<Subscription>

    @POST("rest/v1/subscribeptions")
    suspend fun insertSubscription(@Body subscription: Subscription): Response<Unit>



}