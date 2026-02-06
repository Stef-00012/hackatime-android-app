package com.stefdp.hackatime.network

import com.stefdp.hackatime.network.models.Feature
import com.stefdp.hackatime.network.models.responses.CurrentUserRawHeartbeatsResponse
import com.stefdp.hackatime.network.models.responses.UserHeartbeatSpansResponse
import com.stefdp.hackatime.network.models.responses.UserProjectDetailsResponse
import com.stefdp.hackatime.network.models.responses.UserProjectsResponse
import com.stefdp.hackatime.network.models.responses.UserStatsLast7DaysResponse
import com.stefdp.hackatime.network.models.responses.UserStatsResponse
import com.stefdp.hackatime.network.models.responses.UserStatsTotalSecondsResponse
import com.stefdp.hackatime.network.models.responses.UserTodayDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface HackatimeApiService {
    @GET("users/my/stats")
    suspend fun getCurrentUserStats(
//        @Header("Authorization") authorization: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("filter_by_project") filterByProject: String? = null,
        @Query("features") features: List</*@JvmSuppressWildcards */Feature>? = null,
    ): Response<UserStatsResponse>

    @GET("users/current/stats/last_7_days")
    suspend fun getCurrentUserStatsLast7Days(
        @Query("features") features: List</*@JvmSuppressWildcards */Feature>? = null,
    ): Response<UserStatsLast7DaysResponse>

    @GET("users/my/stats?total_seconds=true")
    suspend fun getCurrentUserStatsTotalSeconds(): Response<UserStatsTotalSecondsResponse>

    @GET("users/{userId}/stats")
    suspend fun getUserStats(
        @Path("userId") userid: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("filter_by_project") filterByProject: String? = null,
        @Query("features") features: List</*@JvmSuppressWildcards */Feature>? = null,
    ): Response<UserStatsResponse>

    @GET("users/current/statusbar/today")
    suspend fun getCurrentUserTodayData(): Response<UserTodayDataResponse>

    @GET("users/{userId}/statusbar/today")
    suspend fun getUserTodayData(
        @Path("userId") userid: String
    ): Response<UserTodayDataResponse>

    @GET("users/{userId}/trust_factor")
    suspend fun getUserTrustFactor(
        @Path("userId") userid: String
    )

    @GET("my/heartbeats")
    suspend fun getCurrentUserRawHeartbeats(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<CurrentUserRawHeartbeatsResponse>

    @GET("my/heartbeats/most_recent")
    suspend fun getCurrentUserMostRecentRawHeartbeats(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<CurrentUserRawHeartbeatsResponse>

    @GET("users/{userId}/heartbeats/spans")
    suspend fun getUserHeartbeatsSpans(
        @Path("userId") userid: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("project") project: String? = null,
        @Query("filter_by_project") filterByProject: List<String>? = null,
    ): Response<UserHeartbeatSpansResponse>

    @GET("users/{userId}/projects")
    suspend fun getUserProjects(
        @Path("userId") userid: String,
    ): Response<UserProjectsResponse>

    @GET("users/{userId}/projects/details")
    suspend fun getUserDetailedProjects(
        @Path("userId") userid: String,
    ): Response<UserProjectDetailsResponse>

    @GET("users/my/projects")
    suspend fun getCurrentUserProjects(): Response<UserProjectsResponse>

    @GET("users/my/projects/details")
    suspend fun getCurrentUserDetailedProjects(): Response<UserProjectDetailsResponse>
}