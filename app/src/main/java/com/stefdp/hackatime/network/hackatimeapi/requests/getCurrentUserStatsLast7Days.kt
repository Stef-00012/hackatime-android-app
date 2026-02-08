package com.stefdp.hackatime.network.hackatimeapi.requests

import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStatsLast7Days
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStatsLast7DaysResponse


private const val authorization = "TEMP"

suspend fun getCurrentUserStatsLast7Days(
    features: List</*@JvmSuppressWildcards */Feature>? = null,
): Result<UserStatsLast7Days> {
    try {
        val response = ApiClient.hackatimeApi.getCurrentUserStatsLast7Days(
            authorization = authorization,
            features = features?.joinToString(",")
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("HackatimeApi[getCurrentUserStatsLast7Days]", "Request failed with code: $statusCode and message: ${response.message()}")

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

        if (body is UserStatsLast7DaysResponse) {
            return Result.success(body.data)
        }

        return Result.failure(
            Exception("Something went wrong...")
        )
    } catch(e: Exception) {
        Log.e("HackatimeApi[getCurrentUserStatsLast7Days]", "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception("Something went wrong...")
        )
    }
}