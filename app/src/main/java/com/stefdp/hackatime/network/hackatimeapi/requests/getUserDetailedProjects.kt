package com.stefdp.hackatime.network.hackatimeapi.requests

import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ProjectDetail
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserProjectDetailsResponse

private const val authorization = "TEMP"

suspend fun getUserDetailedProjects(
    userId: String,
): Result<List<ProjectDetail>> {
    try {
        val response = ApiClient.hackatimeApi.getUserDetailedProjects(
            authorization = authorization,
            userId = userId,
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("HackatimeApi[getUserDetailedProjects]", "Request failed with code: $statusCode and message: ${response.message()}")

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

        if (body is UserProjectDetailsResponse) {
            return Result.success(body.projects)
        }

        return Result.failure(
            Exception("Something went wrong...")
        )
    } catch(e: Exception) {
        Log.e("HackatimeApi[getUserDetailedProjects]", "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception("Something went wrong...")
        )
    }
}