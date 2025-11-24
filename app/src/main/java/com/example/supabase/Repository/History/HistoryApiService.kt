package com.example.supabase.Repository.History

import com.example.supabase.data.History
import com.example.supabase.data.HistoryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface HistoryApiService {

    @POST("rest/v1/historys")
    suspend fun insertHistory(@Body history: HistoryRequest): Response<Unit>

    @GET("rest/v1/historys")
    suspend fun getHistory(@Query("user_id") userId: String): List<History>

    @PATCH("rest/v1/historys")
    @Headers("Prefer: return=representation")
    suspend fun updateHistory(
        @Query("id") idFilter: String,
        @Body history: HistoryRequest
    ): Response<Unit>


    @DELETE("rest/v1/historys")
    suspend fun deleteHistory(@Query("id") id: String): Response<Unit>
}