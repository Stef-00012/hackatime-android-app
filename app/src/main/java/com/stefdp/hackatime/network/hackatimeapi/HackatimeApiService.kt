package com.stefdp.hackatime.network.hackatimeapi

import com.stefdp.hackatime.network.hackatimeapi.models.UserTrustFactor
import com.stefdp.hackatime.network.hackatimeapi.models.responses.CurrentUserRawHeartbeatsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ProjectDetail
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserHeartbeatSpansResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserProjectDetailsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserProjectsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStatsLast7DaysResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStatsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStatsTotalSecondsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserTodayDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

private const val API_VERSION = "v1"

interface HackatimeApiService {
    @GET("${API_VERSION}/users/my/stats")
    suspend fun getCurrentUserStats(
        @Header("Authorization") authorization: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("filter_by_project") filterByProject: String? = null,
        @Query("features") features: String? = null,
    ): Response<UserStatsResponse>

    @GET("hackatime/${API_VERSION}/users/current/stats/last_7_days")
    suspend fun getCurrentUserStatsLast7Days(
        @Header("Authorization") authorization: String,
        @Query("features") features: String? = null,
    ): Response<UserStatsLast7DaysResponse>

    @GET("${API_VERSION}/users/my/stats?total_seconds=true")
    suspend fun getCurrentUserStatsTotalSeconds(
        @Header("Authorization") authorization: String,
    ): Response<UserStatsTotalSecondsResponse>

    @GET("${API_VERSION}/users/{userId}/stats")
    suspend fun getUserStats(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("filter_by_project") filterByProject: String? = null,
        @Query("features") features: String? = null,
    ): Response<UserStatsResponse>

    @GET("hackatime/${API_VERSION}/users/current/statusbar/today")
    suspend fun getCurrentUserTodayData(
        @Header("Authorization") authorization: String,
    ): Response<UserTodayDataResponse>

    @GET("hackatime/${API_VERSION}/users/{userId}/statusbar/today")
    suspend fun getUserTodayData(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String
    ): Response<UserTodayDataResponse>

    @GET("${API_VERSION}/users/{userId}/trust_factor")
    suspend fun getUserTrustFactor(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String
    ): Response<UserTrustFactor>

    @GET("${API_VERSION}/my/heartbeats")
    suspend fun getCurrentUserRawHeartbeats(
        @Header("Authorization") authorization: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<CurrentUserRawHeartbeatsResponse>

    @GET("${API_VERSION}/my/heartbeats/most_recent")
    suspend fun getCurrentUserMostRecentRawHeartbeats(
        @Header("Authorization") authorization: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("limit") limit: Int? = null,
    ): Response<CurrentUserRawHeartbeatsResponse>

    @GET("${API_VERSION}/users/{userId}/heartbeats/spans")
    suspend fun getUserHeartbeatsSpans(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("project") project: String? = null,
        @Query("filter_by_project") filterByProject: String? = null,
    ): Response<UserHeartbeatSpansResponse>

    @GET("${API_VERSION}/users/{userId}/projects")
    suspend fun getUserProjects(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String,
    ): Response<UserProjectsResponse>

    @GET("${API_VERSION}/users/{userId}/projects/details")
    suspend fun getUserDetailedProjects(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String,
    ): Response<UserProjectDetailsResponse>

    @GET("${API_VERSION}/users/my/projects")
    suspend fun getCurrentUserProjects(
        @Header("Authorization") authorization: String,
    ): Response<UserProjectsResponse>

    @GET("${API_VERSION}/users/my/projects/details")
    suspend fun getCurrentUserDetailedProjects(
        @Header("Authorization") authorization: String,
    ): Response<UserProjectDetailsResponse>

    @GET("${API_VERSION}/users/my/project/{projectName}")
    suspend fun getCurrentUserProject(
        @Header("Authorization") authorization: String,
        @Path("projectName") projectName: String,
    ): Response<ProjectDetail>

    @GET("${API_VERSION}/users/{userId}/project/{projectName}")
    suspend fun getUserProject(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String,
        @Path("projectName") projectName: String,
    ): Response<ProjectDetail>

    @GET("${API_VERSION}/ysws_programs")
    suspend fun getYSWSPrograms(): Response<List<String>>
}