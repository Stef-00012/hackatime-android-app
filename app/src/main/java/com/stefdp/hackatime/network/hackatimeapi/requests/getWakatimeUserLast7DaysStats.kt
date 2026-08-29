package com.stefdp.hackatime.network.hackatimeapi.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.hackatime.Logger
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserLast7DaysStatsResponse
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "HackatimeApi[getWakatimeUserLast7DaysStats]"

suspend fun getWakatimeUserLast7DaysStats(
    context: Context,
    userId: String = "current",
): Result<GetWakatimeUserLast7DaysStatsResponse.Data> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        var apiKey = secureStore.get(SecureStorage.STORAGE_API_KEY)

        if (apiKey.isNullOrBlank()) {
            val apiKeysResponse = getOAuthUserApiKeys(context)

            if (apiKeysResponse.isFailure) {
                return Result.failure(
                    Exception(context.getString(R.string.missing_api_key))
                )
            }

            apiKey = apiKeysResponse.getOrNull()?.token ?: return Result.failure(
                Exception(context.getString(R.string.missing_api_key))
            )
        }

        val response = ApiClient.hackatimeApi.getWakatimeUserLast7DaysStats(
            authorization = "Bearer $apiKey",
            userId = userId
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

        if (body is GetWakatimeUserLast7DaysStatsResponse) {
            return Result.success(body.data)
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