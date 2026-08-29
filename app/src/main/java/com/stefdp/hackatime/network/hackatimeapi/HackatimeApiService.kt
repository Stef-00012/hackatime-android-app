package com.stefdp.hackatime.network.hackatimeapi

import com.google.gson.JsonObject
import com.stefdp.hackatime.network.hackatimeapi.models.ProjectDetails
import com.stefdp.hackatime.network.hackatimeapi.models.TrustFactor
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetLeaderboardResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetMyMostRecentHeartbeatsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetOAuthUserResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetOAuthUserApiKeysResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetOAuthUserHoursResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetOAuthUserStreakResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetUserHeartbeatSpansResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetUserProjectNamesResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetUserStatsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetUserTotalSecondsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserLast7DaysStatsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserSummariesResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserTodayDataResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ListCurrentlyHackingUsers
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ListMyHeartbeatsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ListOAuthUserProjectsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ListUserProjectDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

private const val API_VERSION = "v1"

interface HackatimeApiService {
    // Wakatime Compatibility - require api key

    @GET("hackatime/${API_VERSION}/users/{userId}")
    suspend fun getWakatimeUser(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String,
    ): Response<GetWakatimeUserResponse>

    @GET("hackatime/${API_VERSION}/users/{userId}/summaries")
    suspend fun getWakatimeUserSummaries(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String = "current",
        @Query("start") startDate: String,
        @Query("end") endDate: String,
        @Query("project") projects: String? = null,
        @Query("timezone") timezone: String? = null,
    ): Response<GetWakatimeUserSummariesResponse>

    @GET("hackatime/${API_VERSION}/users/{userId}/statusbar/today")
    suspend fun getWakatimeUserTodayData(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String = "current"
    ): Response<GetWakatimeUserTodayDataResponse>

    @GET("hackatime/${API_VERSION}/users/{userId}/stats/last_7_days")
    suspend fun getWakatimeUserLast7DaysStats(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String = "current"
    ): Response<GetWakatimeUserLast7DaysStatsResponse>

    // OAuth2-specific - require access token

    @GET("${API_VERSION}/authenticated/me")
    suspend fun getOAuthUser(
        @Header("Authorization") authorization: String
    ): Response<GetOAuthUserResponse>

    @GET("${API_VERSION}/authenticated/hours")
    suspend fun getOAuthUserHours(
        @Header("Authorization") authorization: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
    ): Response<GetOAuthUserHoursResponse>

    @GET("${API_VERSION}/authenticated/streak")
    suspend fun getOAuthUserStreak(
        @Header("Authorization") authorization: String,
    ): Response<GetOAuthUserStreakResponse>

    @GET("${API_VERSION}/authenticated/projects")
    suspend fun listOAuthUserProjects(
        @Header("Authorization") authorization: String,
        @Query("include_archived") includeArchived: Boolean? = null,
        @Query("projects") projects: String? = null, // comma separated names
        @Query("since") projectsStart: String? = null,
        @Query("until") projectsEnd: String? = null,
        @Query("start") statsStart: String? = null,
        @Query("end") statsEnd: String? = null,
    ): Response<ListOAuthUserProjectsResponse>

    @GET("${API_VERSION}/authenticated/api_keys")
    suspend fun getOAuthUserApiKeys(
        @Header("Authorization") authorization: String,
    ): Response<GetOAuthUserApiKeysResponse>

    @GET("${API_VERSION}/authenticated/heartbeats/latest")
    suspend fun getOAuthUserLatestHeartbeat(
        @Header("Authorization") authorization: String
    ): Response<JsonObject> // either GetOAuthUserLatestHeartbeatsResponse or GetOAuthUserLatestHeartbeatsError

    // Currently hacking - no auth

    @GET("${API_VERSION}/currently_hacking")
    suspend fun listCurrentlyHackingUsers(): Response<ListCurrentlyHackingUsers>

    // Leaderboard - no auth

    @GET("${API_VERSION}/leaderboard/daily")
    suspend fun getDailyLeaderboard(): Response<GetLeaderboardResponse>

    @GET("${API_VERSION}/leaderboard/weekly")
    suspend fun getWeeklyLeaderboard(): Response<GetLeaderboardResponse>

    // My Data - require access token or api key

    @GET("${API_VERSION}/my/heartbeats/most_recent")
    suspend fun getMyMostRecentHeartbeats(
        @Header("Authorization") authorization: String,
        @Query("editor") editor: String? = null,
    ): Response<GetMyMostRecentHeartbeatsResponse>

    @GET("${API_VERSION}/my/heartbeats")
    suspend fun listMyHeartbeats(
         @Header("Authorization") authorization: String,
         @Query("start_date") startDate: String? = null,
         @Query("end_date") endDate: String? = null,
    ): Response<ListMyHeartbeatsResponse>

    // Stats - no auth

    @GET("${API_VERSION}/users/{username}/heartbeats/spans")
    fun getUserHeartbeatSpans(
        @Path("username") username: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("project") project: String? = null,
        @Query("filter_by_project") filterByProject: String? = null, // comma separated projects
    ): Response<GetUserHeartbeatSpansResponse>

    @GET("${API_VERSION}/users/{username}/trust_factor")
    suspend fun getUserTrustFactor(
        @Path("username") username: String
    ): Response<TrustFactor>

    @GET("${API_VERSION}/users/{username}/projects")
    suspend fun getUserProjectNames(
        @Path("username") username: String
    ): Response<GetUserProjectNamesResponse>

    @GET("${API_VERSION}/users/{username}/projects/{projectName}")
    suspend fun getUserProjectDetails(
        @Path("username") username: String,
        @Path("projectName") projectName: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
    ): Response<ProjectDetails>

    @GET("${API_VERSION}/users/{username}/projects/details")
    suspend fun listUserProjectDetails(
        @Path("username") username: String,
        @Query("projects") projects: String? = null, // comma separated project names
        @Query("since") projectsStartDate: String? = null,
        @Query("until") projectsEndDate: String? = null,
        @Query("start") statsStartDate: String? = null,
        @Query("end") statsEndDate: String? = null,
    ): Response<ListUserProjectDetailsResponse>

    @GET("${API_VERSION}/users/{username}/stats")
    suspend fun getUserStats(
        @Header("Authorization") authorization: String? = null, // only needed if username = "my"
        @Path("username") username: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("features") features: String? = null, // comma separated list of Feature
        @Query("filter_by_project") filterByProject: String? = null, // comma separated list of project names
        @Query("filter_by_category") filterByCategory: String? = null, // comma separated list of category names
        @Query("no_ai_coding") noAiCoding: Boolean? = null,
    ): Response<GetUserStatsResponse>

    @GET("${API_VERSION}/users/{username}/stats")
    suspend fun getUserTotalSeconds(
        @Header("Authorization") authorization: String? = null, // only needed if username = "my"
        @Path("username") username: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("features") features: String? = null, // comma separated list of Feature
        @Query("filter_by_project") filterByProject: String? = null, // comma separated list of project names
        @Query("filter_by_category") filterByCategory: String? = null, // comma separated list of category names
        @Query("no_ai_coding") noAiCoding: Boolean? = null,
        @Query("total_seconds") totalSeconds: Boolean = true,
        @Query("boundary_aware") useBoundaryAwakeCalculation: Boolean? = null
    ): Response<GetUserTotalSecondsResponse>
}