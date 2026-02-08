package com.stefdp.hackatime.network.hackatimeapi.requests

import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.HeartbeatSpan
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserHeartbeatSpansResponse

private const val authorization = "TEMP"

suspend fun getUserHeartbeatsSpans(
    userId: String,
    startDate: String? = null,
    endDate: String? = null,
    project: String? = null,
    filterByProject: List<String>? = null,
): Result<List<HeartbeatSpan>> {
    try {
        val response = ApiClient.hackatimeApi.getUserHeartbeatsSpans(
            authorization = authorization,
            userId = userId,
            startDate = startDate,
            endDate = endDate,
            project = project,
            filterByProject = filterByProject?.joinToString(","),
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("HackatimeApi[getUserHeartbeatsSpans]", "Request failed with code: $statusCode and message: ${response.message()}")

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

        if (body is UserHeartbeatSpansResponse) {
            return Result.success(body.spans)
        }

        return Result.failure(
            Exception("Something went wrong...")
        )
    } catch(e: Exception) {
        Log.e("HackatimeApi[getUserHeartbeatsSpans]", "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception("Something went wrong...")
        )
    }
}