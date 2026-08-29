package com.stefdp.hackatime.network.hackatimeapi.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.hackatime.Logger
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.Project
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetOAuthUserHoursResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetOAuthUserResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetOAuthUserStreakResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserLast7DaysStatsResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ListOAuthUserProjectsResponse
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "HackatimeApi[listOAuthUserProjects]"

suspend fun listOAuthUserProjects(
    context: Context,
    includeArchived: Boolean? = null,
    projects: List<String>? = null,
    projectsStart: String? = null,
    projectsEnd: String? = null,
    statsStart: String? = null,
    statsEnd: String? = null
): Result<List<Project>> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        val accessToken = secureStore.get(SecureStorage.STORAGE_ACCESS_TOKEN)

        if (accessToken.isNullOrBlank()) {
            return Result.failure(
                Exception(context.getString(R.string.missing_access_token))
            )
        }

        val response = ApiClient.hackatimeApi.listOAuthUserProjects(
            authorization = "Bearer $accessToken",
            includeArchived = includeArchived,
            projects = projects?.joinToString(","),
            projectsStart = projectsStart,
            projectsEnd = projectsEnd,
            statsStart = statsStart,
            statsEnd = statsEnd
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

        if (body is ListOAuthUserProjectsResponse) {
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