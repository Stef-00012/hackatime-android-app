package com.stefdp.hackatime.network.hackatimeapi.requests

import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStats
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStatsResponse

private const val authorization = "TEMP"

suspend fun getUserStats(
    userId: String,
    startDate: String? = null,
    endDate: String? = null,
    limit: Int? = null,
    filterByProject: String? = null,
    features: List<Feature>? = null,
): Result<UserStats> {
    try {
        val response = ApiClient.hackatimeApi.getUserStats(
            authorization = authorization,
            userId = userId,
            startDate = startDate,
            endDate = endDate,
            limit = limit,
            filterByProject = filterByProject,
            features = features?.joinToString(",")
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("HackatimeApi[getUserStats]", "Request failed with code: $statusCode and message: ${response.message()}")

            if (statusCode == 401) {
                return Result.failure(
                    Exception("Invalid API Key")
                )
            }

            val errorBody = response.errorBody()?.string()
            val json = Gson().fromJson(errorBody, ErrorResponse::class.java)

            if (json.error.isNotEmpty()) {
                return Result.failure(
                    Exception(json.error)
                )
            }

            return Result.failure(
                Exception("Something went wrong...")
            )
        }

        if (body is UserStatsResponse) {
            return Result.success(body.data)
        }

        return Result.failure(
            Exception("Something went wrong...")
        )
    } catch(e: Exception) {
        Log.e("HackatimeApi[getUserStats]", "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception("Something went wrong...")
        )
    }
}