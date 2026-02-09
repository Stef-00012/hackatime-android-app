package com.stefdp.hackatime.network.hackatimeapi.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.HeartbeatSpan
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserHeartbeatSpansResponse
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "HackatimeApi[getUserHeartbeatsSpans]"

suspend fun getUserHeartbeatsSpans(
    context: Context,
    userId: String,
    startDate: String? = null,
    endDate: String? = null,
    project: String? = null,
    filterByProject: List<String>? = null,
): Result<List<HeartbeatSpan>> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        val apiKey = secureStore.get("apiKey")

        if (apiKey == null || apiKey.isEmpty()) {
            return Result.failure(
                Exception("Missing API Key")
            )
        }

        val response = ApiClient.hackatimeApi.getUserHeartbeatsSpans(
            authorization = "Bearer $apiKey",
            userId = userId,
            startDate = startDate,
            endDate = endDate,
            project = project,
            filterByProject = filterByProject?.joinToString(","),
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e(TAG, "Request failed with code: $statusCode and message: ${response.message()}")

            if (statusCode == 401) {
                return Result.failure(
                    Exception("Invalid API Key")
                )
            }

            val errorBody = response.errorBody()?.string()
            val json = Gson().fromJson(errorBody, ErrorResponse::class.java)

            if (json.error.isNotEmpty()) {
                Log.e(TAG, "Error message: ${json.error}")

                return Result.failure(
                    Exception(json.error)
                )
            }

            return Result.failure(
                Exception("Something went wrong...")
            )
        }

        if (body is UserHeartbeatSpansResponse) {
            return Result.success(body.spans)
        }

        return Result.failure(
            Exception("Something went wrong...")
        )
    } catch(e: Exception) {
        Log.e(TAG, "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception("Something went wrong...")
        )
    }
}