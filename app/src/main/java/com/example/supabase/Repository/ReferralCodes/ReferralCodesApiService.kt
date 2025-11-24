package com.example.supabase.Repository.ReferralCodes

import com.example.supabase.data.ReferralCode
import com.example.supabase.data.UserDevice
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ReferralCodesApiService {

//    @GET("rest/v1/referral_codes")
//    suspend fun getReferralCode(): List<ReferralCode>

    @GET("rest/v1/referral_codes")
    suspend fun getReferralCode(
        @Query("referral_code") referralCode: String,
        @Query("select") select: String = "*"
    ): List<ReferralCode>


    @POST("rest/v1/referral_codes")
    suspend fun insertReferralCode(@Body referralCode: ReferralCode): Response<Unit>




}