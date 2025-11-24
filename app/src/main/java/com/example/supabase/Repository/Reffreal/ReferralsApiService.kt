package com.example.supabase.Repository.Reffreal


import com.example.supabase.data.Referral
import com.example.supabase.data.ReferralCode
import com.example.supabase.data.UserDevice
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ReferralsApiService {


    @GET("rest/v1/reffreals")
    suspend fun getReferral(@Query("referrer_id") userId: String): List<Referral>


    @POST("rest/v1/reffreals")
    suspend fun insertReferral(@Body referral: Referral): Response<Unit>




}