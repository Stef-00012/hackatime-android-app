package com.stefdp.hackatime.network.hackatimeoauth

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface HackatimeOAuthService {
    @POST("revoke")
    suspend fun revokeOAuthToken(
        @Query("token") token: String,
        @Query("client_id") clientId: String,
    ): Response<Unit>
}