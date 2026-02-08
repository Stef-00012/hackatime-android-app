package com.stefdp.hackatime.network.hackatimeapi.requests

import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserProjectsResponse

private const val authorization = "TEMP"

suspend fun getUserProjects(
    userId: String,
): Result<List<String>> {
    try {
        val response = ApiClient.hackatimeApi.getUserProjects(
            authorization = authorization,
            userId = userId,
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("HackatimeApi[getUserProjects]", "Request failed with code: $statusCode and message: ${response.message()}")

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

        if (body is UserProjectsResponse) {
            return Result.success(body.projects)
        }

        return Result.failure(
            Exception("Something went wrong...")
        )
    } catch(e: Exception) {
        Log.e("HackatimeApi[getUserProjects]", "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception("Something went wrong...")
        )
    }
}