package com.example.supabase.Repository.UserDevice

import com.example.supabase.data.UserDevice
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query



interface UserDevicesApiService {

//    @GET("rest/v1/referral_codes")
//    suspend fun getReferralCode(): List<ReferralCode>

    @GET("rest/v1/user_devices")
    suspend fun getUserDevice(
        @Query("device_id") device_id: String,
        @Query("select") select: String = "*"
    ): List<UserDevice>

    @POST("rest/v1/user_devices")
    suspend fun insertUserDevice(@Body device: UserDevice): Response<Unit>


}