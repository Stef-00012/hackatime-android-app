package com.stefdp.hackatime.network.backendapi

import com.stefdp.hackatime.network.backendapi.models.GoalDate
import com.stefdp.hackatime.network.backendapi.models.NotificationCategory
import com.stefdp.hackatime.network.backendapi.models.responses.NotificationCategoriesResponse
import com.stefdp.hackatime.network.backendapi.models.responses.DeleteUserResponse
import com.stefdp.hackatime.network.backendapi.models.responses.GetUserGoalsResponse
import com.stefdp.hackatime.network.backendapi.models.responses.GetUserResponse
import com.stefdp.hackatime.network.backendapi.models.responses.SendApiKeyResponse
import com.stefdp.hackatime.network.backendapi.models.responses.SendPushNotificationTokenResponse
import com.stefdp.hackatime.network.backendapi.models.responses.UpdateUserGoalResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface BackendApiService {
    @POST("user")
    suspend fun sendApiKey(
        @Header("Authorization") apiKey: String,
    ): Response<SendApiKeyResponse>

    // expo/token // expo push notification token, unused here since not react native anymore
    @POST("token")
    suspend fun sendPushNotificationToken(
        @Header("Authorization") apiKey: String,
        @Body token: String,
    ): Response<SendPushNotificationTokenResponse>

    @GET("notifications")
    suspend fun getUserNotificationCategories(
        @Header("Authorization") apiKey: String,
    ): Response<NotificationCategoriesResponse>

    @PATCH("notifications")
    suspend fun updateUserNotificationCategories(
        @Header("Authorization") apiKey: String,
//        @Body categories: Map<String, Boolean>
        @Body categories: Map<NotificationCategory, Boolean>,
    ): Response<NotificationCategoriesResponse>

    @GET("user")
    suspend fun getUser(
        @Header("Authorization") apiKey: String,
    ): Response<GetUserResponse>

    @DELETE("user")
    suspend fun deleteUser(
        @Header("Authorization") apiKey: String,
    ): Response<DeleteUserResponse>

    @GET("goals")
    suspend fun getUserGoals(
        @Header("Authorization") apiKey: String,
        @Query("all") all: Boolean? = null,
        @Query("date") date: GoalDate? = null,
        @Query("start_date") startDate: GoalDate? = null,
        @Query("end_date") endDate: GoalDate? = null,
    ): Response<GetUserGoalsResponse>

    @PATCH("goals")
    suspend fun updateUserGoal(
        @Header("Authorization") apiKey: String,
        @Body goal: Int,
        @Body date: GoalDate
    ): Response<UpdateUserGoalResponse>
}