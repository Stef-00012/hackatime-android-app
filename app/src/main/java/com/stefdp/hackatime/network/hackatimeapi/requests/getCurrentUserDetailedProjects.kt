package com.stefdp.hackatime.network.hackatimeapi.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ProjectDetail
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserProjectDetailsResponse
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "HackatimeApi[getCurrentUserDetailedProjects]"

suspend fun getCurrentUserDetailedProjects(
    context: Context
): Result<List<ProjectDetail>> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        val apiKey = secureStore.get("apiKey")

        if (apiKey.isNullOrEmpty()) {
            return Result.failure(
                Exception(context.getString(R.string.missing_api_key))
            )
        }

        val response = ApiClient.hackatimeApi.getCurrentUserDetailedProjects(
            authorization = "Bearer $apiKey",
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e(TAG, "Request failed with code: $statusCode and message: ${response.message()}")

            if (statusCode == 401) {
                return Result.failure(
                    Exception(context.getString(R.string.invalid_api_key))
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
                Exception(context.getString(R.string.generic_error))
            )
        }

        if (body is UserProjectDetailsResponse) {
            return Result.success(body.projects)
        }

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    } catch(e: Exception) {
        Log.e(TAG, "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    }
}