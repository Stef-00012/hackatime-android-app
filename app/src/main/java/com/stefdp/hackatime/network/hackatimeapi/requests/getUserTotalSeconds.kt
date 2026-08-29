package com.stefdp.hackatime.network.hackatimeapi.requests

import android.R.attr.apiKey
import android.content.Context
import com.google.gson.Gson
import com.stefdp.hackatime.Logger
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.models.ProjectDetails
import com.stefdp.hackatime.network.hackatimeapi.models.TrustFactor
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetLeaderboardResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetUserHeartbeatSpansResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetUserProjectNamesResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetUserStatsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetUserTotalSecondsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserSummariesResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.Heartbeat
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ListCurrentlyHackingUsers
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ListUserProjectDetailsResponse
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "HackatimeApi[getUserTotalSeconds]"

suspend fun getUserTotalSeconds(
    context: Context,
    username: String,
    startDate: String? = null,
    endDate: String? = null,
    limit: Int? = null,
    features: List<Feature>? = null,
    filterByProject: List<String>? = null,
    filterByCategory: List<String>? = null,
    noAiCoding: Boolean? = null,
): Result<Long> {
    try {
        val response = if (username == "my") {
            val secureStore = SecureStorage.getInstance(context)

            val accessToken = secureStore.get(SecureStorage.STORAGE_ACCESS_TOKEN)

            if (accessToken.isNullOrBlank()) {
                return Result.failure(
                    Exception(context.getString(R.string.missing_access_token))
                )
            }

            ApiClient.hackatimeApi.getUserTotalSeconds(
                authorization = "Bearer $accessToken",
                username = username,
                startDate = startDate,
                endDate = endDate,
                limit = limit,
                features = features?.joinToString(","),
                filterByProject = filterByProject?.joinToString(","),
                filterByCategory = filterByCategory?.joinToString(","),
                noAiCoding = noAiCoding
            )
        } else {
            ApiClient.hackatimeApi.getUserTotalSeconds(
                username = username,
                startDate = startDate,
                endDate = endDate,
                limit = limit,
                features = features?.joinToString(","),
                filterByProject = filterByProject?.joinToString(","),
                filterByCategory = filterByCategory?.joinToString(","),
                noAiCoding = noAiCoding
            )
        }

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Logger.error(TAG, "Request failed with code: $statusCode and message: ${response.message()}")

            if (statusCode == 401) {
                return Result.failure(
                    Exception(context.getString(R.string.invalid_api_key))
                )
            }

            val errorBody = response.errorBody()?.string()
            val json = Gson().fromJson(errorBody, ErrorResponse::class.java)

            if (json.error.isNotEmpty()) {
                Logger.error(TAG, "Error message: ${json.error}")

                return Result.failure(
                    Exception(json.error)
                )
            }

            return Result.failure(
                Exception(context.getString(R.string.generic_error))
            )
        }

        if (body is GetUserTotalSecondsResponse) {
            return Result.success(body.totalSeconds)
        }

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    } catch(e: Exception) {
        Logger.error(TAG, "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    }
}