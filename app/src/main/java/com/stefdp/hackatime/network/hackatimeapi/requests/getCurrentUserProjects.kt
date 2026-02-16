package com.stefdp.hackatime.network.hackatimeapi.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserProjectsResponse
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "HackatimeApi[getCurrentUserProjects]"

suspend fun getCurrentUserProjects(
    context: Context
): Result<List<String>> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        val apiKey = secureStore.get("apiKey")

        if (apiKey.isNullOrEmpty()) {
            return Result.failure(
                Exception("Missing API Key")
            )
        }

        val response = ApiClient.hackatimeApi.getCurrentUserProjects(
            authorization = "Bearer $apiKey",
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

        if (body is UserProjectsResponse) {
            return Result.success(body.projects)
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