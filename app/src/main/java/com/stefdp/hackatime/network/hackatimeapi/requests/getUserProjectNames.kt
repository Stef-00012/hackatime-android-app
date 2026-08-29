package com.stefdp.hackatime.network.hackatimeapi.requests

import android.R.attr.apiKey
import android.content.Context
import com.google.gson.Gson
import com.stefdp.hackatime.Logger
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.TrustFactor
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetLeaderboardResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetUserHeartbeatSpansResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetUserProjectNamesResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserSummariesResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.Heartbeat
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ListCurrentlyHackingUsers
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "HackatimeApi[getUserProjectNames]"

suspend fun getUserProjectNames(
    context: Context,
    username: String
): Result<List<String>> {
    try {
        val response = ApiClient.hackatimeApi.getUserProjectNames(
            username = username
        )

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

        if (body is GetUserProjectNamesResponse) {
            return Result.success(body.projects)
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