package com.stefdp.hackatime.network.hackatimeoauth.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.hackatime.HACKATIME_OAUTH_CLIENT_ID
import com.stefdp.hackatime.Logger
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetOAuthUserHoursResponse
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "HackatimeOAuth[revokeOAuthToken]"

suspend fun revokeOAuthToken(
    context: Context
): Result<Boolean> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        val accessToken = secureStore.get(SecureStorage.STORAGE_ACCESS_TOKEN)

        if (accessToken.isNullOrBlank()) {
            return Result.failure(
                Exception(context.getString(R.string.missing_access_token))
            )
        }

        val response = ApiClient.hackatimeOAuth.revokeOAuthToken(
            clientId = HACKATIME_OAUTH_CLIENT_ID,
            token = accessToken
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

        return Result.success(true)
    } catch(e: Exception) {
        Logger.error(TAG, "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    }
}